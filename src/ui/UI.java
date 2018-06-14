package ui;

import com.mkk.swing.JTimeChooser;
import util.Constants;
import util.FileUtil;
import util.GBC;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UI extends JFrame {

    private JPanel mJPanel;
    private static JPanel checkBoxs;
    
    private JButton sourceBtn = new JButton("浏览");
    private JButton targetBtn = new JButton("浏览");
    private JButton submitBtn = new JButton("开始处理");
    private JButton changeTimeBtn = new JButton("时间");
    private JTextField sourceJtf = new JTextField();
    private JTextField targetJtf = new JTextField();
    private JTextField changeTimeJtf = new JTextField();

    JFileChooser fileChooser = new JFileChooser();

    private static String mSourcePath;
    private static String mTargetPath;
    private String selectedFileType = "";

    File srcFile = null;
    File targetFile = null;

    public UI(){

        //可选择目录
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        setVisible(true);
        setSize(600, 420);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        mJPanel = new JPanel();
        mJPanel.setLayout(new GridBagLayout());
        add(mJPanel);

        //处理路径
        JLabel sourceLbl = new JLabel("处理路径: ");
        sourceJtf.setPreferredSize(new Dimension(370, 30));
        mJPanel.add(new JPanel(), new GBC(0,0,1,1).setWeight(100, 0));
        mJPanel.add(sourceLbl, new GBC(1,0,1,1).setWeight(100, 100));
        mJPanel.add(sourceJtf, new GBC(2,0,5,1).setWeight(100, 100));
        mJPanel.add(sourceBtn, new GBC(7,0,1,1).setWeight(100, 100));
        mJPanel.add(new JPanel(), new GBC(8,0,1,1).setWeight(100, 0));

        //目标路径
        JLabel targetLbl = new JLabel("目标路径: ");
        targetJtf.setPreferredSize(new Dimension(370, 30));
        mJPanel.add(new JPanel(), new GBC(0,1,1,1).setWeight(100, 0));
        mJPanel.add(targetLbl, new GBC(1,1,1,1).setWeight(100, 100));
        mJPanel.add(targetJtf, new GBC(2,1,5,1).setWeight(100, 100));
        mJPanel.add(targetBtn, new GBC(7,1,1,1).setWeight(100, 100));
        mJPanel.add(new JPanel(), new GBC(8,1,1,1).setWeight(100, 0));

        //文件格式
        JLabel typeLabel = new JLabel("文件格式: ");
        mJPanel.add(new JPanel(), new GBC(0,2,1,1).setWeight(100, 0));
        mJPanel.add(typeLabel, new GBC(1,2,1,1).setWeight(100, 100));
        checkBoxs = getCheckBox();
        mJPanel.add(checkBoxs, new GBC(2,2,5,1).setWeight(100, 100));
        mJPanel.add(new JPanel(), new GBC(7,2,2,1).setWeight(100, 0));

        //修改时间
        JLabel timeLabel = new JLabel("修改时间: ");
        changeTimeJtf.setEditable(false);
        changeTimeJtf.setBackground(Color.white);
        changeTimeJtf.setPreferredSize(new Dimension(370, 30));
        mJPanel.add(new JPanel(), new GBC(0,3,1,1).setWeight(100, 0));
        mJPanel.add(timeLabel, new GBC(1,3,1,1).setWeight(100, 100));
        mJPanel.add(changeTimeJtf, new GBC(2,3,5,1).setWeight(100, 100));
        mJPanel.add(changeTimeBtn, new GBC(7,3,1,1).setWeight(100, 100));
        mJPanel.add(new JPanel(), new GBC(8,3,1,1).setWeight(100, 0));

        //提交按钮
        mJPanel.add(new JPanel(), new GBC(0,4,4,1).setWeight(100, 0));
        mJPanel.add(submitBtn, new GBC(1,4,2,1).setWeight(100, 100));
        mJPanel.add(new JPanel(), new GBC(3,4,3,1).setWeight(100, 0));

        //按钮监听事件
        sourceBtn.addActionListener(new ActionHandler());
        targetBtn.addActionListener(new ActionHandler());
        submitBtn.addActionListener(new ActionHandler());
        changeTimeBtn.addActionListener(new ActionHandler());

    }

    public class ActionHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            //确定按钮
            if(e.getSource() == submitBtn){
                submitBtnDo();
            }
            //处理路径 浏览按钮
            else if(e.getSource() == sourceBtn){
                fileChooser.showOpenDialog(mJPanel);
                srcFile = fileChooser.getSelectedFile();
                if(null != srcFile){
                    sourceJtf.setText(srcFile.getAbsolutePath());
                }
            }
            //目标路径 浏览按钮
            else if(e.getSource() == targetBtn){
                fileChooser.showOpenDialog(mJPanel);
                targetFile = fileChooser.getSelectedFile();
                //如果文件夹不存在 弹出提示框是否创建
                if (null != targetFile && !targetFile.exists()) {
                    targetFile.mkdirs();
                    JOptionPane.showMessageDialog(mJPanel, "所选路径不存在，已在相应文件夹下创建。", "错误提醒",JOptionPane.WARNING_MESSAGE);
                }
                targetJtf.setText(targetFile.getAbsolutePath());
            }
            else if(e.getSource() == changeTimeBtn){
                JTimeChooser timeChooser = new JTimeChooser(mJPanel);
                Calendar calendar = timeChooser.showTimeDialog();
                changeTimeJtf.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
            }
        }
    }

    /**
     * 确定按钮操作
     */
    private void submitBtnDo()
    {
        mSourcePath = sourceJtf.getText().toString();
        mTargetPath = targetJtf.getText().toString();
        String changeTime = changeTimeJtf.getText().toString();
        selectedFileType = getSelectedFileTypes();

        if(null == mSourcePath || "".equals(mSourcePath) || null == mTargetPath || "".equals(mTargetPath))
        {
            JOptionPane.showMessageDialog(this, "处理路径或目标路径未输入", "错误提醒",JOptionPane.WARNING_MESSAGE);
            return;
        }

        if(mSourcePath.indexOf(mTargetPath) > 0 || mTargetPath.indexOf(mSourcePath) > 0)
        {
            JOptionPane.showMessageDialog(this, "目标路径在处理路径中，请重新选择路径", "错误提醒",JOptionPane.WARNING_MESSAGE);
            return;
        }

        if("".equals(selectedFileType))
        {
            JOptionPane.showMessageDialog(this, "请选择文件格式", "错误提醒",JOptionPane.WARNING_MESSAGE);
            return;
        }

        if("".equals(changeTime))
        {
            JOptionPane.showMessageDialog(this, "请填写修改时间", "错误提醒",JOptionPane.WARNING_MESSAGE);
            return;
        }

        File file = new File(mSourcePath);
        if(!file.exists()){
            JOptionPane.showMessageDialog(this, "处理路径不存在！", "错误提醒",JOptionPane.WARNING_MESSAGE);
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
        if (checkBoxs == null) {
            checkBoxs = new JPanel();// 创建面板对象
            checkBoxs.setLayout(new GridLayout(0, 6));// 设置网格布局管理器

            List<Constants.FileType> fileTypes = Constants.FileType.getEnumToList();
            JCheckBox[] boxs = new JCheckBox[fileTypes.size()];
            for (int i = 0; i < fileTypes.size(); i++)
            {
                boxs[i] = new JCheckBox(fileTypes.get(i).getText());
                checkBoxs.add(boxs[i]);
            }
        }
        return checkBoxs;
    }

    /**
     * 获取已选择文件格式
     */
    private String getSelectedFileTypes()
    {
        selectedFileType = "";
        Component[] components = checkBoxs.getComponents();
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
        sourceJtf.setEnabled(isCanUsed);
        targetJtf.setEnabled(isCanUsed);
        changeTimeJtf.setEnabled(isCanUsed);
        sourceBtn.setEnabled(isCanUsed);
        targetBtn.setEnabled(isCanUsed);
        submitBtn.setEnabled(isCanUsed);

        Component[] components = checkBoxs.getComponents();
        for (Component component : components) {
            JCheckBox jcb = (JCheckBox) component;
            jcb.setEnabled(isCanUsed);
        }

        if(isCanUsed){
            submitBtn.setText("开始处理");
        }else{
            submitBtn.setText("正在处理，请稍后...");
        }
    }

}