package verification.uppaal.verify;


import verification.uppaal.model.NTA;

import java.io.*;

public class Verifyta {

    private static String command = "verifyta -S0 ";

    public static Result check(String trace, String ntaPath, String propertyPath) throws IOException {
        String cmd = command + trace + " " + ntaPath + " " + propertyPath;
        Process process = Runtime.getRuntime().exec(cmd);
        InputStreamReader stream = new InputStreamReader(process.getInputStream());
        BufferedReader br = new BufferedReader(stream);
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
//            System.out.println(line);
            if (line.contains("NOT")) {
                sb.append(line).append("\n");
                break;
            }
            sb.append(line).append("\n");
        }

        stream = new InputStreamReader(process.getErrorStream());
        br = new BufferedReader(stream);
        while ((line = br.readLine()) != null) {

            sb.append(line).append("\n");
        }
        return new Result(sb.toString());
    }

    /**
     * 校验nta模型是否满足 给定的statement
     */
    public static Result isSatisfied(NTA nta, String statement) throws IOException {
        String base = ".\\src\\main\\resources\\verification\\";
//        String trace = " -t1 -f " + base + "trace";
        String trace = " -t1" + base + "trace";
        String ntaPath = base + nta.getName() + ".xml";
        nta.writeToUppaalXml(ntaPath);
        String statementPath = base + "nta.q";
        writeProperty(statementPath, statement);

        return check(trace, ntaPath, statementPath);
    }

    public static void writeProperty(String path, String state) {
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(path));
            writer.write(state);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        String ntaPath = ".\\src\\main\\resources\\verification\\nta.xml";
        String propertyPath = ".\\src\\main\\resources\\verification\\nta.q";
        String base = ".\\src\\main\\resources\\verification\\";
        String trace = " -t1" + base + "trace";
        Result result = Verifyta.check(trace, ntaPath, propertyPath);
        System.out.println(result);
    }
}
