package com.itheima.controller;


import com.itheima.config.EtherpadConfig;
import com.itheima.mapper.ClassMapper;
import com.itheima.mapper.GroupMapper;
import com.itheima.mapper.UserMapper;
import com.itheima.pojo.*;
import com.itheima.service.TaskService;
import com.itheima.service.UserService;
import com.itheima.utils.AliOSSUtils;
import com.itheima.utils.ExcelUtils;
import com.itheima.utils.ReadStudentExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.itheima.utils.renderResult.renderResult;

@Slf4j
@RestController
@RequestMapping(("/upload"))
public class UploadController {
    //本地存储文件的方式
//    @PostMapping("/upload")
//    public Result upload(String username, Integer age, MultipartFile image) throws IOException {
//        log.info("文件上传：{},{},{}", username, age, image);
//        //获取原始文件名 - 再获取拓展名
//        String originalFilename = image.getOriginalFilename();
//
//        //构造唯一的文件名（不能重复） - uuid（通用唯一识别码）2ffccebe-fe46-4520-aa5a-5303e0cc4176+拓展名
//        int index = originalFilename.lastIndexOf(".");
//        String extname = originalFilename.substring(index);
//        String newFileNmae = UUID.randomUUID().toString() + extname;
//        log.info("新的文件名：{}", newFileNmae);
//
//        //将文件存储在服务器的磁盘目录中
//        image.transferTo(new File("D:\\images\\" + newFileNmae));
//
//        return Result.success();
//    }
    @Autowired
    private AliOSSUtils aliOSSUtils;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ClassMapper classMapper;
    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private EtherpadConfig etherpadConfig;


    @PostMapping
    public Result upload(MultipartFile file) throws IOException {
        log.info("文件上传，文件名：{}", file.getOriginalFilename());

        //调用阿里云oss工具类进行文件上传
        String url = aliOSSUtils.upload(file);

        log.info("文件上传完成，文件访问的url为：{}", url);

        return Result.success(url);
    }

    @Transactional
    @PostMapping("/excelExport")
    public void excelExport(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        long startTime = System.currentTimeMillis();
        List<String> list = new ArrayList();
        Map<String, Object> res = new HashMap<>();
        int row = 1;
        int rowSuccess = 0;
        Integer errorCount = 0;
        Boolean flag = false;
        List<Student> excelInfo = ReadStudentExcelUtil.getExcelInfo(file);
        for (Student student : excelInfo) {
            row++;
            //业务代码，通过学生用户名查询数据库中式是否有该学生，如果已存在，则不保存，跳过该学生
            Integer studentCount = userMapper.getStudentByUsername(student.getUsername());
            if (studentCount > 0) {
                list.add("在第" + row + "行的<" + student.getUsername() + ">学生已存在");
                errorCount++;
                continue;
            }
            //业务代码，通过教师用户名查询数据库中是否有该教师，如果没有，则不保存，跳过该学生
            if (student.getTeacherID()==null){
                list.add("在第" + row + "行的学生教师id未填写，请检查教师id");
                continue;
            }else {
                Integer teacherCount = userMapper.getTeacherByID(student.getTeacherID());
                if (teacherCount == 0){
                    list.add("在第" + row + "行的学生教师不存在，请检查教师id");
                    errorCount++;
                    continue;
                }
            }
            //业务代码，通过班级id查询数据库中是否有该班级，如果没有，则不保存，跳过该学生
            if(student.getClassId()!=null){
                Classpojo classCount = classMapper.getById(student.getClassId());
                if (classCount == null) {
                    list.add("在第" + row + "行的学生班级不存在，请检查班级和分组");
                    errorCount++;
                    continue;
                }else{
                    if(!classCount.getTeacherId().equals(student.getTeacherID())){
                        list.add("在第" + row + "行的学生班级与教师不匹配，请检查班级和教师");
                        errorCount++;
                        continue;
                    }
                }
            }
            //业务代码，通过分组id查询数据库中是否有该分组，如果没有，则不保存，跳过该学生
            if (student.getGroupId()!=null){
                Group group = groupMapper.single(student.getGroupId());
                if (group == null) {
                    list.add("在第" + row + "行的学生分组不存在");
                    errorCount++;
                    continue;
                }
            }
            //业务代码，通过班级id查询小组，查看学生小组是否在该班级内，如果不在，则不保存，跳过该学生
            if (student.getClassId()!= null && student.getGroupId()!= null){
                List<Group> groupList = groupMapper.listByClassId(student.getClassId());
                for (Group groupItem : groupList) {
                    if (groupItem.getId().equals(student.getGroupId())) {
                        flag = true;
                        break;
                    }
                }
                if (!flag){
                    list.add("在第" + row + "行的学生分组不在该班级内，请检查班级和分组");
                    errorCount++;
                    continue;
                }
            }
            //学生用户名、班级id、小组id都合理，添加该学生到数据库
            Boolean save = userService.add(student);
            if (save) {
                rowSuccess++;
            }
        }
        if (list.size() > 0) {
            res.put("log", list);
        }
        res.put("success", "导入数据成功条数:" + rowSuccess);
        res.put("error", "导入数据失败条数:" + errorCount);
        long endTime = System.currentTimeMillis();
        String time = String.valueOf((endTime - startTime) / 1000);
        res.put("time", "导入数据用时:" + time + "秒");
        renderResult(response, res);
    }

    @GetMapping("/messageExport")
    public void export(Integer chatRoomID, HttpServletResponse response) throws Exception {
        List<Message> list = taskService.getMessageRecord(chatRoomID);

        ExcelUtils.exportExcelToTarget(response, null, "Excel导入演示", list, Message.class);
    }

    @GetMapping("/padAndMessageExport")
    public void padAndMessageExport(@RequestParam Integer taskID, HttpServletResponse response, @RequestParam String authorId) throws Exception {
        Task task = taskService.getTaskById(taskID);
        // 获取该次任务班级的所有小组信息
        List<Group> groupList = groupMapper.listByClassId(task.getClassID());
        // 获取班级完整信息，用来获取班级名称
        Classpojo classInfo = classMapper.getById(task.getClassID());

        // 创建临时文件夹
        String tempDirPath = Files.createTempDirectory("task_export_").toString();
        Path tempDir = Paths.get(tempDirPath);

        // 最终文件命名：班级名称-任务名称-时间戳.zip
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String zipFileName = timestamp + ".zip";
        Path zipFilePath = tempDir.resolve(zipFileName);

        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFilePath.toFile()))) {
            for (Group group : groupList) {
                String sessionID = userService.createEtherpadSession(authorId, group.getGroupID());
                TaskInfoOfGroup taskInfo = taskService.getTaskInfo(taskID, group.getId());
                Integer chatRoomID = taskInfo.getChatRoomID();
                List<Message> list = taskService.getMessageRecord(chatRoomID);

                // 创建小组文件夹
                Path groupDir = Files.createDirectories(tempDir.resolve(group.getName()));

                // 导出聊天记录 Excel 文件
                InputStream exportExcelInputStream = ExcelUtils.exportExcelInputStream(group.getName() + "-聊天记录", null, list, Message.class);
                Path excelFilePath = groupDir.resolve(group.getName() + "-聊天记录.xlsx");
                Files.copy(exportExcelInputStream, excelFilePath);
                exportExcelInputStream.close();

                // 导出写作任务信息
                List<Integer> procedureIdList = taskService.getCollabrotiveIdList(task.getId(), group.getId());
                for (Integer procedureId : procedureIdList) {
                    CollaborativeProcedure collaborativeProcedure = taskService.getCollabrotiveProcedureInfo(procedureId);
                    if (collaborativeProcedure == null){
                        continue;
                    }
                    String padID = collaborativeProcedure.getPadID();
                    String urlString = etherpadConfig.getEtherpadUrl() + "/p/" + padID + "/export/html" + "?apikey=" + etherpadConfig.getApikey();
                    StringBuilder content = new StringBuilder();
                    try {
                        URL url = new URL(urlString);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                        connection.setRequestProperty("Cookie", "sessionID=" + sessionID);

                        try (BufferedReader reader = new BufferedReader(
                                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                content.append(line);
                                content.append(System.lineSeparator());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // 保存 HTML 文件
                    Path htmlFilePath = groupDir.resolve(group.getName() + "-写作任务-" + padID + ".html");
                    Files.write(htmlFilePath, content.toString().getBytes(StandardCharsets.UTF_8));
                }
                // 将小组文件夹添加到 ZIP 文件中
                addToZip(groupDir, group.getName(), zipOut);
            }
        }
        // 设置响应头
        response.setContentType("application/zip");
        // 设置响应头，使用 RFC 6266 的 filename* 参数
        String encodedFileName = URLEncoder.encode(zipFileName, StandardCharsets.UTF_8.toString()).replace("+", "%20");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipFileName + "\"; filename*=UTF-8''" + encodedFileName);
        // 将 ZIP 文件写入响应
        Files.copy(zipFilePath, response.getOutputStream());
        // 删除临时文件夹
        deleteDirectory(tempDir);
    }

    private void addToZip(Path source, String entryName, ZipOutputStream zipOut) throws IOException {
        if (Files.isDirectory(source)) {
            for (Path file : Files.newDirectoryStream(source)) {
                addToZip(file, entryName + "/" + file.getFileName().toString(), zipOut);
            }
        } else {
            ZipEntry zipEntry = new ZipEntry(entryName);
            zipOut.putNextEntry(zipEntry);
            Files.copy(source, zipOut);
            zipOut.closeEntry();
        }
    }

    private void deleteDirectory(Path directory) throws IOException {
        Files.walk(directory)
                .sorted((p1, p2) -> -p1.compareTo(p2))
                .map(Path::toFile)
                .forEach(File::delete);
    }
}


