package util;

import java.io.*;
import java.nio.file.Files;
import java.util.Date;

public class FileUtil {

    /**
     * 文件处理
     * @param selectedFileType 所选文件类型 如：xml,java
     * @param sourcePath 文件路径
     * @param changeTime 修改时间
     * @param mSourcePath 处理路径
     * @param mTargetPath 目标路径
     */
    public static void doFile(String selectedFileType, String sourcePath, Date changeTime, String mSourcePath, String mTargetPath) {
        iteratorPath(selectedFileType, sourcePath, changeTime, mSourcePath, mTargetPath);
        deleteEmpty(mTargetPath);
    }

    /**
     * 遍历文件夹 拷贝在修改时间之后修改的文件到目标路径
     */
    private static void iteratorPath(String selectedFileType, String sourcePath, Date changeTime, String mSourcePath, String mTargetPath)
    {
        try{
            File sourceFiles = new File(sourcePath);
            File[] files = sourceFiles.listFiles();
            String[] types = selectedFileType.split(",");
            if (files != null && files.length > 0)
            {
                String newFilePath;
                for (File file : files)
                {
                    //替换处路径为目标路径
                    newFilePath = file.getAbsolutePath().replace(mSourcePath, mTargetPath);
                    File newFile = new File(newFilePath);
                    //文件且最后更新时间在填写的修改时间之后
                    if (file.isFile() && file.lastModified() > changeTime.getTime())
                    {
                        String fileName = file.getName();
                        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
                        //文件格式在所选文件类型之中
                        if(isSelectedType(types, suffix)){
                            //如在目标路径存在该文件删除文件
                            newFile.delete();
                            //将处理路径中文件拷贝到目标路径
                            Files.copy(file.toPath(), new File(newFilePath).toPath());
                        }
                    }
                    else if (file.isDirectory())
                    {
                        //当前路径为文件夹，在目标路径中创建文件夹
                        if(!newFile.exists()) {
                            boolean result = newFile.mkdirs();
                            if(result){
                                //遍历该文件夹
                                iteratorPath(selectedFileType, file.getAbsolutePath(), changeTime, mSourcePath, mTargetPath);
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static boolean isSelectedType(String[] types, String fileType)
    {
        for (String type : types) {
            if(fileType.equals(type)){
                return true;
            }
        }
        return false;
    }

    //删除空文件夹
    private static void deleteEmpty(String path)
    {
        try {
            File file = new File(path);
            //空文件夹 进行删除
            if(file.isDirectory()){
                File[] files = file.listFiles();
                if (null == files || files.length < 1){
                    file.delete();
                }
                else{
                    for (File f : files){
                        if(f.isDirectory()){
                            deleteEmpty(f.getAbsolutePath());
                        }
                    }
                    files = file.listFiles();
                    if (null == files || files.length < 1){
                        file.delete();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}