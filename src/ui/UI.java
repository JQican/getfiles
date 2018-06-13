package ui;

import util.Constants;
import util.FileUtil;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UI {

    private JFrame jFrame = null;
    private static JPanel mJPanel;

    private JButton btnSrc, btnDest, btnSubmit;
    private JTextField jtaSrc, jtaDest, jtaTime;

    JFileChooser fileChooser = new JFileChooser();

    private static String mSourcePath;
    private static String mTargetPath;
    private String selectedFileType = "";

    File srcFile = null;
    File targetFile = null;

    public UI(){

        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);//可选择目录

        jFrame = new JFrame("Get Files");
        jFrame.setVisible(true);
        jFrame.setSize(650, 410);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLocationRelativeTo(null);
        jFrame.setResizable(false);
        //jFrame.setLayout(new BorderLayout());

        //处理路径
        JLabel sourceLabel = new JLabel("处理路径: ");
        jtaSrc = new JTextField();
        jtaSrc.setMaximumSize(new Dimension(476, 30));
        jtaSrc.setMinimumSize(new Dimension(476, 30));
        btnSrc = new JButton("浏览");
        btnSrc.addActionListener(new ActionHandler());

        //目标路径
        JLabel targetLabel = new JLabel("目标路径: ");
        jtaDest = new JTextField();
        jtaDest.setMaximumSize(new Dimension(476, 30));
        jtaDest.setMinimumSize(new Dimension(476, 30));
        btnDest = new JButton("浏览");
        btnDest.addActionListener(new ActionHandler());

        //文件格式
        JLabel typeLabel = new JLabel("文件格式: ");
        mJPanel = getCheckBox();

        //修改时间
        JLabel timeLabel = new JLabel("修改时间: ");
        JLabel timeFormat = new JLabel("时间格式：2018-05-01 13:39:42 或 2018/05/01 13:39:42");
        timeFormat.setForeground(Color.red);
        jtaTime = new JTextField();
        jtaTime.setMaximumSize(new Dimension(300, 30));
        jtaTime.setMinimumSize(new Dimension(300, 30));
        btnSubmit = new JButton("开始处理");
        btnSubmit.addActionListener(new ActionHandler());

        //处理路径
        Box hBoxSource = Box.createHorizontalBox();
        hBoxSource.add(Box.createHorizontalStrut(10));
        hBoxSource.add(sourceLabel);
        hBoxSource.add(Box.createHorizontalStrut(10));
        hBoxSource.add(jtaSrc);
        hBoxSource.add(Box.createHorizontalStrut(10));
        hBoxSource.add(btnSrc);
        hBoxSource.add(Box.createHorizontalStrut(10));

        //目标路径
        Box hBoxTarget = Box.createHorizontalBox();
        hBoxTarget.add(Box.createHorizontalStrut(10));
        hBoxTarget.add(targetLabel);
        hBoxTarget.add(Box.createHorizontalStrut(10));
        hBoxTarget.add(jtaDest);
        hBoxTarget.add(Box.createHorizontalStrut(10));
        hBoxTarget.add(btnDest);
        hBoxTarget.add(Box.createHorizontalStrut(10));

        //文件格式
        Box hBoxFileType = Box.createHorizontalBox();
        hBoxFileType.add(Box.createHorizontalStrut(10));
        hBoxFileType.add(typeLabel);
        hBoxFileType.add(Box.createHorizontalStrut(10));
        hBoxFileType.add(mJPanel);
        hBoxFileType.add(Box.createHorizontalGlue());

        //修改时间
        Box hBoxTime = Box.createHorizontalBox();
        hBoxTime.add(Box.createHorizontalStrut(10));
        hBoxTime.add(timeLabel);
        hBoxTime.add(Box.createHorizontalStrut(10));
        hBoxTime.add(jtaTime);
        hBoxTime.add(Box.createHorizontalStrut(10));
        hBoxTime.add(timeFormat);
        hBoxTime.add(Box.createHorizontalStrut(10));

        //提交
        Box hBoxSubmit = Box.createHorizontalBox();
        hBoxSubmit.add(Box.createHorizontalStrut(10));
        hBoxSubmit.add(btnSubmit);
        hBoxSubmit.add(Box.createHorizontalStrut(10));
        hBoxSubmit.add(Box.createHorizontalGlue());

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel,BoxLayout.Y_AXIS));
        centerPanel.add(hBoxSource);
        centerPanel.add(hBoxTarget);
        centerPanel.add(hBoxFileType);
        centerPanel.add(hBoxTime);
        centerPanel.add(hBoxSubmit);
        jFrame.getContentPane().add(centerPanel);

    }

    public class ActionHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            //确定按钮
            if(e.getSource() == btnSubmit){
                btnSubmitDo();
            }
            //处理路径 浏览按钮
            else if(e.getSource() == btnSrc){
                fileChooser.showOpenDialog(jFrame);
                srcFile = fileChooser.getSelectedFile();
                if(null != srcFile){
                    jtaSrc.setText(srcFile.getAbsolutePath());
                }
            }
            //目标路径 浏览按钮
            else if(e.getSource() == btnDest){
                fileChooser.showOpenDialog(jFrame);
                targetFile = fileChooser.getSelectedFile();
                //如果文件夹不存在 弹出提示框是否创建
                if (null != targetFile && !targetFile.exists()) {
                    targetFile.mkdirs();
                    JOptionPane.showMessageDialog(jFrame, "所选路径不存在，已在相应文件夹下创建。", "错误提醒",JOptionPane.WARNING_MESSAGE);
                }
                jtaDest.setText(targetFile.getAbsolutePath());
            }
        }
    }

    /**
     * 确定按钮操作
     */
    private void btnSubmitDo()
    {
        mSourcePath = jtaSrc.getText().toString();
        mTargetPath = jtaDest.getText().toString();
        String changeTime = jtaTime.getText().toString();
        selectedFileType = getSelectedFileTypes();

        if(null == mSourcePath || "".equals(mSourcePath) || null == mTargetPath || "".equals(mTargetPath))
        {
            JOptionPane.showMessageDialog(jFrame, "处理路径或目标路径未输入", "错误提醒",JOptionPane.WARNING_MESSAGE);
            return;
        }

        if(mSourcePath.indexOf(mTargetPath) > 0 || mTargetPath.indexOf(mSourcePath) > 0)
        {
            JOptionPane.showMessageDialog(jFrame, "目标路径在处理路径中，请重新选择路径", "错误提醒",JOptionPane.WARNING_MESSAGE);
            return;
        }

        if("".equals(selectedFileType))
        {
            JOptionPane.showMessageDialog(jFrame, "请选择文件格式", "错误提醒",JOptionPane.WARNING_MESSAGE);
            return;
        }

        if("".equals(changeTime))
        {
            JOptionPane.showMessageDialog(jFrame, "请填写修改时间", "错误提醒",JOptionPane.WARNING_MESSAGE);
            return;
        }

        File file = new File(mSourcePath);
        if(!file.exists()){
            JOptionPane.showMessageDialog(jFrame, "处理路径不存在！", "错误提醒",JOptionPane.WARNING_MESSAGE);
            return;
        }

        File target = new File(mTargetPath);
        if (!target.exists() && !target.mkdirs()) {
            System.out.println(target.getAbsolutePath());
            target.mkdirs();
        }
        try {
            boolean isFinished = false;
            setElementDisabled(isFinished);
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(changeTime);
            isFinished = FileUtil.doFile(selectedFileType, mSourcePath, date, mSourcePath, mTargetPath);
            if(isFinished){
                setElementDisabled(true);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 文件格式复选框面板
     */
    public static JPanel getCheckBox()
    {
        if (mJPanel == null) {
            mJPanel = new JPanel();// 创建面板对象
            mJPanel.setLayout(new GridLayout(0, 6));// 设置网格布局管理器

            List<Constants.FileType> fileTypes = Constants.FileType.getEnumToList();
            JCheckBox[] boxs = new JCheckBox[fileTypes.size()];
            for (int i = 0; i < fileTypes.size(); i++)
            {
                boxs[i] = new JCheckBox(fileTypes.get(i).getText());
                mJPanel.add(boxs[i]);
            }
        }
        return mJPanel;
    }

    /**
     * 获取已选择文件格式
     */
    private String getSelectedFileTypes()
    {
        selectedFileType = "";
        Component[] components = mJPanel.getComponents();
        for (Component component : components) {
            JCheckBox jcb = (JCheckBox) component;
            if(jcb.isSelected()) {
                selectedFileType += jcb.getText() + ",";
            }
        }
        return selectedFileType;
    }

    /**
     * 禁用或启用所有元素
     */
    private void setElementDisabled(boolean isCanUsed)
    {
        jtaSrc.setEnabled(isCanUsed);
        jtaDest.setEnabled(isCanUsed);
        jtaTime.setEnabled(isCanUsed);
        btnSrc.setEnabled(isCanUsed);
        btnDest.setEnabled(isCanUsed);
        btnSubmit.setEnabled(isCanUsed);

        Component[] components = mJPanel.getComponents();
        for (Component component : components) {
            JCheckBox jcb = (JCheckBox) component;
            jcb.setEnabled(isCanUsed);
        }

        if(isCanUsed){
            btnSubmit.setText("开始处理");
        }else{
            btnSubmit.setText("正在处理，请稍后...");
        }
    }

}
