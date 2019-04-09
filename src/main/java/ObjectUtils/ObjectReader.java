package ObjectUtils;

import com.esotericsoftware.yamlbeans.YamlReader;
import org.json.JSONObject;
import org.openqa.selenium.By;

import java.io.FileReader;
import java.util.Map;

public class ObjectReader {

    protected static JSONObject readYaml(String fileName, String absolutePath) throws Exception {
        YamlReader yamlReader = new YamlReader(new FileReader(absolutePath + fileName));
        Map objectMap = (Map) yamlReader.read();
        return new JSONObject(objectMap);
    }

    protected static String getUrl(JSONObject object, String urlKey) {
        return object.getJSONObject("Objects").getString(urlKey);
    }

    protected static By getElement(JSONObject object, String element, String page) {
        String locator = object.getJSONObject("Objects").getJSONObject(page).getJSONObject(element).keys().next();
        String value = object.getJSONObject("Objects").getJSONObject(page).getJSONObject(element).getString(locator);
        By locatorValue = null;
        if (locator.equalsIgnoreCase("className"))
            locatorValue = By.className(value);
        else if (locator.equalsIgnoreCase("cssSelector"))
            locatorValue = By.cssSelector(value);
        else if (locator.equalsIgnoreCase("id"))
            locatorValue = By.id(value);
        else if (locator.equalsIgnoreCase("tagName"))
            locatorValue = By.tagName(value);
        else if (locator.equalsIgnoreCase("xpath"))
            locatorValue = By.xpath(value);
        else if (locator.equalsIgnoreCase("linkText"))
            locatorValue = By.linkText(value);
        else if (locator.equalsIgnoreCase("name"))
            locatorValue = By.name(value);
        else if (locator.equalsIgnoreCase("partialLinkText"))
            locatorValue = By.partialLinkText(value);
        else new Exception("objectTypeException: Please enter correct object locator type");
        return locatorValue;
    }

}
