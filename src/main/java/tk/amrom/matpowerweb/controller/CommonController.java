package tk.amrom.matpowerweb.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mathworks.toolbox.javabuilder.MWStructArray;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mathworks.toolbox.javabuilder.MWApplication;
import com.mathworks.toolbox.javabuilder.MWCharArray;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

import tk.amrom.matpower.LoadCase;
import tk.amrom.matpowerweb.MatpowerWebApplication;

@RestController
@RequestMapping("/v1/common")
public class CommonController {

    private final MatpowerWebApplication mySpringBootApplication;

    public CommonController(MatpowerWebApplication mySpringBootApplication) {
        this.mySpringBootApplication = mySpringBootApplication;
    }

    @PostMapping("/loadcase")
    @ResponseBody
    public ResponseEntity<String>  loadCase(@RequestParam(value = "pdValues", required = false) double[] pdValues){
        //清空上次输出缓存
        mySpringBootApplication.clearCapturedConsoleOutput();
        // double[] initPdValues = { 55, 3, 41, 0, 13, 75, 0, 150, 121, 5, 0, 377, 18, 10.5, 22, 43, 42,
        //     27.2, 3.3, 2.3, 0, 0, 6.3, 0, 6.3, 0, 9.3, 4.6, 17, 3.6, 5.8, 1.6,
        //     3.8, 0, 6, 0, 0, 14, 0, 0, 6.3, 7.1, 2, 12, 0, 0, 29.7, 0, 18,
        //     21, 18, 4.9, 20, 4.1, 6.8, 7.6, 6.7 };
        if (pdValues == null || pdValues.length == 0) {
            // pdValues = initPdValues;
            return ResponseEntity.badRequest().body("pdValues cannot be null or empty");
        }
        if (pdValues.length != 57) {
            return ResponseEntity.badRequest().body("pdValues length must be 57");
        }
        try {

            if (!MWApplication.isMCRInitialized()) {
                MWApplication.initialize();
            }

            LoadCase loadCase = new LoadCase();

            MWNumericArray mwPdValues = new MWNumericArray(pdValues, MWClassID.DOUBLE);
            Object[] element = loadCase.LoadCase(1, mwPdValues);
            // 确保输出流被刷新
            System.out.flush();
            if (element[0] instanceof MWStructArray) {
                // MWStructArray structArray = (MWStructArray) element[0];
                // JsonObject jsonObject = parseResult(structArray, 1);
                // return ResponseEntity.ok(jsonObject.toString());
                String input = mySpringBootApplication.getCapturedConsoleOutput();
                String branchData = extractBranchData(input);

                return ResponseEntity.ok().body(branchData);
            }

            mwPdValues.dispose();
            loadCase.dispose();

            return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body("出错了1");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body("出错了2");
    }

    public static String extractBranchData(String input) {
        // 使用正则表达式找到 "Branch Data" 开始的位置
        Pattern pattern = Pattern.compile("Branch Data(.*)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            // 获取 "Branch Data" 后的所有内容
            String branchSection = matcher.group(1).trim();

            // 去掉头三行（分隔符和表头行）
            String[] lines = branchSection.split("\n");
            StringBuilder cleanedData = new StringBuilder();

            // 跳过前三行，并将其余的行重新组合
            for (int i = 3; i < lines.length; i++) {
                cleanedData.append(lines[i]).append("\n");
            }

            return cleanedData.toString().trim();
        } else {
            return "Branch Data not found";
        }
    }

    // 主方法 - 解析 Matlab 结果结构并构造 JSON 对象
    public JsonObject parseResult(MWStructArray structArray, int index) throws MWException {
        JsonObject jsonResult = new JsonObject();

        // 解析 "version"
        jsonResult.addProperty("version", getStringValue(structArray.get("version", index)));

        // 解析 "baseMVA"
        jsonResult.addProperty("baseMVA", 100);

        // 解析 "bus" - 二维数组
        jsonResult.add("bus", getArrayValue(structArray.get("bus", index)));

        // 解析 "gen" - 二维数组
        jsonResult.add("gen", getArrayValue(structArray.get("gen", index)));

        // 解析 "branch" - 二维数组
        jsonResult.add("branch", getArrayValue(structArray.get("branch", index)));

        // 解析 "gencost" - 二维数组
        jsonResult.add("gencost", getArrayValue(structArray.get("gencost", index)));

         // 解析 "et" - 二维数组
         jsonResult.add("et", getArrayValue(structArray.get("et", index)));

        // 解析 "success" - 二维数组
        jsonResult.add("success", getArrayValue(structArray.get("success", index)));

        return jsonResult;
    }

    // Helper method to handle string values
    private String getStringValue(Object obj) {
        if (obj instanceof MWCharArray) {
            return ((MWCharArray) obj).toString();
        } else if (obj instanceof char[][]) {
            StringBuilder result = new StringBuilder();
            for (char[] row : (char[][]) obj) {
                result.append(row);
            }
            return result.toString();
        }
        return obj != null ? obj.toString() : null;
    }

    // Helper method to handle numeric arrays (e.g., double[][])
    private JsonArray getArrayValue(Object obj) {
        JsonArray jsonArray = new JsonArray();

        if (obj instanceof double[][]) {
            double[][] numericArray = (double[][]) obj; // 正确的类型转换
            for (double[] row : numericArray) { // 使用 numericArray
                JsonArray rowArray = new JsonArray();
                for (double value : row) {
                    rowArray.add(value);
                }
                jsonArray.add(rowArray);
            }
        }
        return jsonArray;
    }

}
