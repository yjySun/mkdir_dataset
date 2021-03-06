package com.yjy.action;

import com.yjy.gui.MyFrame;
import com.yjy.pojo.Dataset;
import com.yjy.template.SubscriberJavaTemplate;
import com.yjy.util.ReadExcel;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

/**
 * @Author: Jiyuan Yao
 * @Date: 2021/9/17 9:47
 * @Description: 生成 abc-dataset-cdc-subscriber 所需要的Java文件
 */
public class SubscriberJavaFile {

    public static void generateSubscriberJavaFile(String filePath, String version, int startIndex) throws Exception {
        String[] split1 = filePath.split("\\\\");
        String fileName = split1[split1.length - 1];//获取文件名

        FileSystemView fsv = FileSystemView.getFileSystemView();
        File com = fsv.getHomeDirectory();
        String deskTopPath = com.getPath() + "\\subscriberJavaFile\\";//获取桌面路径

        List<Dataset> datasets = ReadExcel.readExcel(filePath);

        if (datasets == null) {
            return;
        } else {
            File file = new File(deskTopPath);
            if (!file.exists()) {
                file.mkdir();
            }

            JLabel analyseLabel = MyFrame.analyseLabel;
            for (int i = 0; i < datasets.size(); i++) {
                Dataset dataset = datasets.get(i);

                String packageEnd = dataset.getDatasetId().split("_")[0];

                for (int j = 0; j < dataset.getTableName().size(); j++) {
                    String datasetId = dataset.getDatasetIdDetail().get(j);
                    String name = dataset.getName();
                    String tableName = dataset.getTableName().get(j);

                    String javaFileName = setJavaFileName(datasetId, startIndex);
                    String datasetContent = SubscriberJavaTemplate.setDataSetSubscriberFile(javaFileName, datasetId, name, tableName, version, packageEnd);

                    FileWriter datasetSubscriberFileWriter = new FileWriter(deskTopPath + javaFileName + ".java");
                    datasetSubscriberFileWriter.write(datasetContent);
                    datasetSubscriberFileWriter.close();

                    if (i == 0) {
                        analyseLabel.setText(analyseLabel.getText() + "------从" + fileName + "中提取数据------" + "<br>");
                        analyseLabel.setText(analyseLabel.getText() + "准备解析数据...." + "<br>");
                    }
                    analyseLabel.setText(analyseLabel.getText() + datasetId + " 解析完成" + "<br>");
                    if (i == datasets.size() - 1 && j == dataset.getTableName().size() - 1) {
                        analyseLabel.setText(analyseLabel.getText() + "------消费者所需Java文件生成完毕------" + "<br>");
                    }
                }

            }
        }
    }

    public static String setJavaFileName(String datasetId, int startIndex) {
        String[] datasetIdSplit = datasetId.split("");
        StringBuffer javaFileName = new StringBuffer();
        for (int i = startIndex; i < datasetIdSplit.length; i++) {
            datasetIdSplit[startIndex] = datasetIdSplit[startIndex].toUpperCase();
            if (datasetIdSplit[i].equals("_")) {
                i++;
                String s = datasetIdSplit[i].toUpperCase();
                javaFileName.append(s);
            } else {
                javaFileName.append(datasetIdSplit[i]);
            }
        }
        javaFileName.append("Subscriber");
        return javaFileName.toString();
    }

}

