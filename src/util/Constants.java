package util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Constants
{
    public enum FileType
    {
        Java(1, "java"),
        Xml(2, "xml"),
        Jsp(3, "jsp"),
        Css(4, "css"),
        Js(5, "js"),
        Xlsx(6, "xlsx"),
        Html(7, "html"),
        Class(6, "class");

        private int key;
        private String text;
        private static final Map<Integer, FileType> intToEnum = new HashMap<Integer, FileType>();
        private static final Map<String, FileType> stringToEnum = new HashMap<String, FileType>();
        private static final List<FileType> enumToList = new ArrayList<FileType>();
        static
        {
            for(FileType type : values())
            {
                intToEnum.put(type.key, type);
                stringToEnum.put(type.toString(), type);
                enumToList.add(type);
            }
        }
        FileType(int key, String text)
        {
            this.key = key;
            this.text = text;
        }
        public static List<FileType> getEnumToList()
        {
            return enumToList;
        }
        public int getKey()
        {
            return this.key;
        }
        public String getText()
        {
            return this.text;
        }
        public static FileType FromString(String symbol)
        {
            return stringToEnum.get(symbol);
        }
        public static FileType FromKey(Integer key)
        {
            return intToEnum.get(key);
        }
        @Override
        public String toString()
        {
            return text;
        }
    }
}
