package experiment.project;

public class Drools implements Project {

    public static String projectName = "drools";
    public static String projectPath = "dataset/drools/";
    public static String rtmClassPath = projectPath + "rtm/RTM_CLASS.txt";
    public static String reqPath = projectPath + "req";
    public static String classDirPath = projectPath + "code";

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