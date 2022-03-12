package com.java.test;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

/**
 * @author Dagon0577
 * @date 2019/12/26 15:11
 */
public class Main {

    public static void main(String[] args) throws Exception {
        String cpuid = getCpuId();
        System.out.println(cpuid);
    }

    static {
        System.out.println("java版本号：" + System.getProperty("java.version")); // java版本号
        System.out.println("Java提供商名称：" + System.getProperty("java.vendor")); // Java提供商名称
        System.out.println("Java提供商网站：" + System.getProperty("java.vendor.url")); // Java提供商网站
        System.out.println("jre目录：" + System.getProperty("java.home")); // Java，哦，应该是jre目录
        System.out.println("Java虚拟机规范版本号：" + System.getProperty("java.vm.specification.version")); // Java虚拟机规范版本号
        System.out.println("Java虚拟机规范提供商：" + System.getProperty("java.vm.specification.vendor")); // Java虚拟机规范提供商
        System.out.println("Java虚拟机规范名称：" + System.getProperty("java.vm.specification.name")); // Java虚拟机规范名称
        System.out.println("Java虚拟机版本号：" + System.getProperty("java.vm.version")); // Java虚拟机版本号
        System.out.println("Java虚拟机提供商：" + System.getProperty("java.vm.vendor")); // Java虚拟机提供商
        System.out.println("Java虚拟机名称：" + System.getProperty("java.vm.name")); // Java虚拟机名称
        System.out.println("Java规范版本号：" + System.getProperty("java.specification.version")); // Java规范版本号
        System.out.println("Java规范提供商：" + System.getProperty("java.specification.vendor")); // Java规范提供商
        System.out.println("Java规范名称：" + System.getProperty("java.specification.name")); // Java规范名称
        System.out.println("Java类版本号：" + System.getProperty("java.class.version")); // Java类版本号
        System.out.println("Java类路径：" + System.getProperty("java.class.path")); // Java类路径
        System.out.println("Java lib路径：" + System.getProperty("java.library.path")); // Java lib路径
        System.out.println("Java输入输出临时路径：" + System.getProperty("java.io.tmpdir")); // Java输入输出临时路径
        System.out.println("Java编译器：" + System.getProperty("java.compiler")); // Java编译器
        System.out.println("Java执行路径：" + System.getProperty("java.ext.dirs")); // Java执行路径
        System.out.println("操作系统名称：" + System.getProperty("os.name")); // 操作系统名称
        System.out.println("操作系统的架构：" + System.getProperty("os.arch")); // 操作系统的架构
        System.out.println("操作系统版本号：" + System.getProperty("os.version")); // 操作系统版本号
        System.out.println("文件分隔符：" + System.getProperty("file.separator")); // 文件分隔符
        System.out.println("路径分隔符：" + System.getProperty("path.separator")); // 路径分隔符
        System.out.println("直线分隔符：" + System.getProperty("line.separator")); // 直线分隔符
        System.out.println("操作系统用户名：" + System.getProperty("user.name")); // 用户名
        System.out.println("操作系统用户的主目录：" + System.getProperty("user.home")); // 用户的主目录
        System.out.println("当前程序所在目录：" + System.getProperty("user.dir")); // 当前程序所在目录
    }
    /**
     * 获取主板序列号
     *
     * @return
     */
    public static String getMotherboardSN() {
        String result = "";
        try {
            File file = File.createTempFile("realhowto", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);

            String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
                    + "Set colItems = objWMIService.ExecQuery _ \n"
                    + "   (\"Select * from Win32_BaseBoard\") \n"
                    + "For Each objItem in colItems \n" + "    Wscript.Echo objItem.SerialNumber \n"
                    + "    exit for  ' do the first cpu only! \n" + "Next \n";

            fw.write(vbs);
            fw.close();
            String path = file.getPath().replace("%20", " ");
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + path);
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;
            }
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.trim();
    }

    /**
     * 获取CPU序列号
     *
     * @return
     */
    public static String getCPUSerial() {
        String result = "";
        try {
            File file = File.createTempFile("tmp", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);
            String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
                    + "Set colItems = objWMIService.ExecQuery _ \n"
                    + "   (\"Select * from Win32_Processor\") \n"
                    + "For Each objItem in colItems \n" + "    Wscript.Echo objItem.ProcessorId \n"
                    + "    exit for  ' do the first cpu only! \n" + "Next \n";

            // + "    exit for  \r\n" + "Next";
            fw.write(vbs);
            fw.close();
            String path = file.getPath().replace("%20", " ");
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + path);
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;
            }
            input.close();
            file.delete();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        if (result.trim().length() < 1 || result == null) {
            result = "无CPU_ID被读取";
        }
        return result.trim();
    }

    /**
     * 获取localhost的LANAddress
     *
     * @return
     */
    private static List<String> getLocalHostLANAddress()
            throws UnknownHostException, SocketException {
        List<String> ips = new ArrayList<String>();
        Enumeration<NetworkInterface> interfs = NetworkInterface.getNetworkInterfaces();
        while (interfs.hasMoreElements()) {
            NetworkInterface interf = interfs.nextElement();
            Enumeration<InetAddress> addres = interf.getInetAddresses();
            while (addres.hasMoreElements()) {
                InetAddress in = addres.nextElement();
                if (in instanceof Inet4Address) {
                    System.out.println("v4:" + in.getHostAddress());
                    if (!"127.0.0.1".equals(in.getHostAddress())) {
                        ips.add(in.getHostAddress());
                    }
                }
            }
        }
        return ips;
    }

    /**
     * MAC
     * 通过jdk自带的方法,先获取本机所有的ip,然后通过NetworkInterface获取mac地址
     *
     * @return
     */
    public static String getMac() {
//        MacUtil.getMac().forEach(System.out::println);
//        List<String> tlsit = MacUtil.getMac1();
//        System.out.println(tlsit);
//
//        System.out.println("1--->"+MacUtil.getInternetIp());
//        System.out.println("2--->"+MacUtil.getIntranetIp());

        String string = "";
        try {
            String resultStr = "";
            List<String> ls = getLocalHostLANAddress();
            int num = 0;
            for (String str : ls) {
                InetAddress ia = InetAddress.getByName(str);// 获取本地IP对象
                // 获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。
                byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
                // 下面代码是把mac地址拼装成String
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < mac.length; i++) {
                    if (i != 0) {
                        //sb.append("-");
                    }
                    // mac[i] & 0xFF 是为了把byte转化为正整数
                    String s = Integer.toHexString(mac[i] & 0xFF);
                    sb.append(s.length() == 1 ? 0 + s : s);
                }
                if (num == ls.size() - 1) {
                    resultStr += sb.toString().toUpperCase();
                } else {
                    // 把字符串所有小写字母改为大写成为正规的mac地址并返回
                    resultStr += sb.toString().toUpperCase() + "-";
                }
                num++;
                System.out.println(sb.toString().toUpperCase());
            }
            string =  resultStr;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("设备MAC地址:"+string);
        return string;
    }

    /***************************linux*********************************/

    public static String executeLinuxCmd(String cmd) {
        try {
            System.out.println("got cmd job : " + cmd);
            Runtime run = Runtime.getRuntime();
            Process process;
            process = run.exec(cmd);
            InputStream in = process.getInputStream();
            BufferedReader bs = new BufferedReader(new InputStreamReader(in));
            StringBuffer out = new StringBuffer();
            byte[] b = new byte[8192];
            for (int n; (n = in.read(b)) != -1; ) {
                out.append(new String(b, 0, n));
            }
            in.close();
            process.destroy();
            return out.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param cmd    命令语句
     * @param record 要查看的字段
     * @param symbol 分隔符
     * @return
     */
    public static String getSerialNumber(String cmd, String record, String symbol) {
        String execResult = executeLinuxCmd(cmd);
        String[] infos = execResult.split("\n");

        for (String info : infos) {
            info = info.trim();
            if (info.indexOf(record) != -1) {
                info.replace(" ", "");
                String[] sn = info.split(symbol);
                return sn[1];
            }
        }

        return null;
    }

    /**
     * @param cmd    命令语句
     * @param record 要查看的字段
     * @param symbol 分隔符
     * @return
     */
    public static String getAllSerialNumber(String cmd, String record, String symbol) {
        String execResult = executeLinuxCmd(cmd);
        String[] infos = execResult.split("\n");
        StringBuilder result = new StringBuilder();
        int k = 0;
        for (int i = 0; i < infos.length - 1; i++) {
            String info = infos[i];
            info = info.trim();
            if (info.indexOf(record) != -1) {
                info.replace(" ", "");
                String[] sn = info.split(symbol);
                if (k != 0) {
                    result.append(',');
                }
                result.append(sn[1]);
                k++;
            }
        }
        if (k != 0) {
            return result.toString();
        } else {
            return null;
        }
    }

    /**
     * 判断是否为容器、虚拟机，返回虚拟ID
     *
     * @return
     */
    public static String getVirtualID() {
        String execResult = executeLinuxCmd("systemd-detect-virt");
        if (!execResult.contains("none")) {
            //docker容器
            String VirtualID = getSerialNumber("cat /proc/1/cgroup", "docker", "docker/");
            if (VirtualID != null) {
                return VirtualID;
            }
            //machine-rkt
            VirtualID = getSerialNumber("cat /proc/1/cgroup", "machine-rkt", "machine-rkt\\");
            if (VirtualID != null) {
                VirtualID.replaceAll("\\x2d", "-");
                return VirtualID;
            }
            //vmware
            VirtualID = getSerialNumber("dmidecode -t system", "UUID", ":");
            if (VirtualID != null) {
                return VirtualID;
            }
            return "UNKNOWN";
        }
        return null;
    }

    /**
     * 获取CPUID、硬盘序列号、MAC地址、主板序列号
     *
     * @return
     */
    public static Map<String, String> getAllSn() {
        String os = System.getProperty("os.name");
        os = os.toUpperCase();
        System.out.println(os);

        Map<String, String> snVo = new HashMap<String, String>();

        if ("LINUX".equals(os)) {
            snVo.put("operating system", "LINUX");
            System.out.println("=============>for linux");
            String virtualID = getVirtualID();
            if (virtualID != null) {
                if (virtualID.equals("UNKNOWN")) {
                    System.out.println("UNKNOWN VMWARE!");
                    return snVo;
                } else {
                    System.out.println("virtualID : " + virtualID);
                    snVo.put("virtualID", virtualID.toUpperCase().replace(" ", ""));
                    String mac = getAllSerialNumber("ifconfig -a", "ether", " ");
                    System.out.println("mac : " + mac);
                    snVo.put("mac", mac.toUpperCase().replace(" ", ""));
                }
            } else {
                String cpuid = getSerialNumber("dmidecode -t processor | grep 'ID'", "ID", ":");
                System.out.println("cpuid : " + cpuid);
                String mainboardNumber =
                        getSerialNumber("dmidecode |grep 'Serial Number'", "Serial Number", ":");
                System.out.println("mainboardNumber : " + mainboardNumber);
                String mac = getAllSerialNumber("ifconfig -a", "ether", " ");
                System.out.println("mac : " + mac);

                snVo.put("cpuid", cpuid.toUpperCase().replace(" ", ""));
                snVo.put("mac", mac.toUpperCase().replace(" ", ""));
                snVo.put("mainboard", mainboardNumber.toUpperCase().replace(" ", ""));
            }
        } else {
            snVo.put("operating system", "windows");
            System.out.println("=============>for windows");
            String cpuid = getCPUSerial();
            String mainboard = getMotherboardSN();
            //String disk = SerialNumberUtil.getHardDiskSN("c");
            String mac = getMac();

            System.out.println("CPU  SN:" + cpuid);
            System.out.println("主板  SN:" + mainboard);
            //System.out.println("C盘   SN:" + disk);
            System.out.println("MAC  SN:" + mac);

            snVo.put("cpuid", cpuid.toUpperCase().replace(" ", ""));
            //snVo.put("diskid", disk.toUpperCase().replace(" ", ""));
            snVo.put("mac", mac.toUpperCase().replace(" ", ""));
            snVo.put("mainboard", mainboard.toUpperCase().replace(" ", ""));
        }
        return snVo;
    }



    /**
     * 获取当前系统CPU序列，可区分linux系统和windows系统
     * cat /proc/cpuinfo |grep "Serial"|awk {'print $3'}
     * system_profiler SPHardwareDataType |grep "Serial"|awk {'print $4'}
     */
    public static String getCpuId() throws Exception {
        String cpuId = null;
        // 获取当前操作系统名称
        String os = System.getProperty("os.name");
        os = os.toLowerCase();
        System.out.println(os);
        // linux系统用Runtime.getRuntime().exec()执行 dmidecode -t processor 查询cpu序列
        // windows系统用 wmic cpu get ProcessorId 查看cpu序列
        if (os.contains("linux")) {
            cpuId = executeLinuxCmd("uname -a");
            System.out.println("---->"+cpuId);
            if(cpuId.toLowerCase().contains("raspberry")){
                System.out.println("树莓派");
                cpuId = getLinuxCpuId("cat /proc/cpuinfo |grep Serial", "Serial", ":");
            }else{
                System.out.println("linux系统");
                cpuId = getLinuxCpuId("dmidecode -t processor | grep 'ID'", "ID", ":");
            }
        }else if(os.contains("windows")){
            cpuId = getWindowsCpuId();
        }else if(os.contains("mac os")){
            cpuId = getLinuxCpuId("system_profiler SPHardwareDataType |grep Serial", "Serial", ":");
            System.out.println("macos");
        } else {
        }
        if (cpuId == null)
            return null;
        return cpuId.toUpperCase().replace(" ", "");
    }

    /**
     * 获取linux系统CPU序列
     */
    public static String getLinuxCpuId(String cmd, String record, String symbol) throws Exception {
        String execResult = executeLinuxCmd(cmd);
        String[] infos = execResult.split("\n");
        for (String info : infos) {
            info = info.trim();
            if (info.indexOf(record) != -1) {
                info.replace(" ", "");
                String[] sn = info.split(symbol);
                return sn[1];
            }
        }
        return null;
    }

    public static String executeLinuxCmds(String cmd) throws Exception {
        Runtime run = Runtime.getRuntime();
        Process process;
        process = run.exec(cmd);
        InputStream in = process.getInputStream();
        BufferedReader bs = new BufferedReader(new InputStreamReader(in));
        StringBuffer out = new StringBuffer();
        byte[] b = new byte[8192];
        for (int n; (n = in.read(b)) != -1; ) {
            out.append(new String(b, 0, n));
        }
        in.close();
        process.destroy();
        return out.toString();
    }

    /**
     * 获取windows系统CPU序列
     */
    public static String getWindowsCpuId() throws Exception {
        Process process = Runtime.getRuntime().exec(
                new String[]{"wmic", "cpu", "get", "ProcessorId"});
        process.getOutputStream().close();
        Scanner sc = new Scanner(process.getInputStream());
        sc.next();
        String serial = sc.next();
        return serial;
    }
}

