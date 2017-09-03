import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
/**
 * javaԶ�̵�½linuxϵͳ�������Ĵ���
 * ��д�ű�����ֱ�Ӿ����ڱ���һ��
 * @author hy
 *
 */
public class Shell {
    //Զ��������ip��ַ
    private String ip;
    //Զ��������¼�û���
    private String username;
    //Զ�������ĵ�¼����
    private String password;
    //����ssh���ӵ�Զ�̶˿�
    public static final int DEFAULT_SSH_PORT = 22;  
    //����������ݵ�����
    private ArrayList<String> stdout;

    /**
     * ��ʼ����¼��Ϣ
     * @param ip
     * @param username
     * @param password
     */
    public Shell(final String ip, final String username, final String password) {
         this.ip = ip;
         this.username = username;
         this.password = password;
         stdout = new ArrayList<String>();
    }
    /**
     * ִ��shell����
     * @param command
     * @return
     */
    public int execute(final String command) {
        int returnCode = 0;
        JSch jsch = new JSch();
        MyUserInfo userInfo = new MyUserInfo();

        try {
            //����session���Ҵ����ӣ���Ϊ����session֮��Ҫ����������
            Session session = jsch.getSession(username, ip, DEFAULT_SSH_PORT);
            session.setPassword(password);
            session.setUserInfo(userInfo);
            session.connect();

            //��ͨ��������ͨ�����ͣ���ִ�е�����
            Channel channel = session.openChannel("exec");
            ChannelExec channelExec = (ChannelExec)channel;
            channelExec.setCommand(command);

            channelExec.setInputStream(null);
            BufferedReader input = new BufferedReader(new InputStreamReader
                    (channelExec.getInputStream()));

            channelExec.connect();
            System.out.println("The remote command is :" + command);

            //����Զ�̷�����ִ������Ľ��
            String line;
            while ((line = input.readLine()) != null) {  
                stdout.add(line);  
            }  
            input.close();  

            // �õ�returnCode
            if (channelExec.isClosed()) {  
                returnCode = channelExec.getExitStatus();  
            }  

            // �ر�ͨ��
            channelExec.disconnect();
            //�ر�session
            session.disconnect();

        } catch (JSchException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnCode;
    }
    /**
     * get stdout
     * @return
     */
    public ArrayList<String> getStandardOutput() {
        return stdout;
    }

    public static void main(final String [] args) {  
    	
        Shell shell = new Shell("192.168.1.115", "root", "123456");
        String pidPath="/home/computer/openface-0.2.0/data/code/server_id.txt";

        String cmd=
        "var=$(cat "+pidPath+")\n"+
        "echo $var\n"+
        "kill -9 $var";

        shell.execute(cmd);
        ArrayList<String> stdout = shell.getStandardOutput();
        for (String str : stdout) {  
            System.out.println(str);  
        }  
    }  
}