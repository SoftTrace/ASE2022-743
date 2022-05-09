package experiment.project;


public class iTrust implements Project {

    public static String projectName = "itrust";
    public static String projectPath = "dataset/itrust/";
    public static String rtmClassPath = projectPath + "rtm/RTM_CLASS.txt";
    public static String reqPath = projectPath + "req";
    public static String classDirPath = projectPath + "code";
    public static String relationPath = projectPath + "relation/CLASS_relationInfo_whole.ser";

    @Override
    public String getRtmClassPath() {
        return rtmClassPath;
    }

    @Override
    public String getReqPath() {
        return reqPath;
    }

    @Override
    public String getClassDirPath() {
        return classDirPath;
    }

    @Override
    public String getProjectName() {
        return projectName;
    }

    @Override
    public String getProjectPath() {
        return projectPath;
    }
}
