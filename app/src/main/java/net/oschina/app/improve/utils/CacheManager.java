package net.oschina.app.improve.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;

import net.oschina.app.improve.app.AppOperator;
import net.oschina.common.utils.StreamUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static com.google.gson.internal.$Gson$Preconditions.checkArgument;
import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;
import static com.google.gson.internal.$Gson$Types.canonicalize;
import static com.google.gson.internal.$Gson$Types.typeToString;

/**
 * Created by haibin
 * on 2016/11/7.
 * change by fei
 * on 2016/12/9
 */

public final class CacheManager {

    public static <T> void saveToJson(Context context, String fileName, List<T> list) {
        if (context == null || list == null || list.size() <= 0)
            return;
        String path = context.getCacheDir() + "/" + fileName + ".json";
        File file = new File(path);
        try {
            if (!file.exists() && !file.createNewFile()) {
                return;
            }
            save(file, list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * save cache file
     *
     * @param file file
     * @param list list
     */
    private static <T> void save(File file, List<T> list) {
        Writer writer = null;
        try {
            writer = new FileWriter(file);
            AppOperator.getGson().toJson(list, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            StreamUtil.close(writer);
        }
    }

    public static void saveToJson(Context context, String fileName, Object object) {
        String json = new Gson().toJson(object);
        String path = context.getCacheDir() + "/" + fileName;
        File file = new File(path);
        FileOutputStream os = null;
        try {
            if (!file.exists())
                file.createNewFile();
            os = new FileOutputStream(file);
            os.write(json.getBytes("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            StreamUtil.close(os);
        }
    }

    public static <T> T readFromJson(Context context, String fileName, Class cla) {
        return readJson(context, fileName, cla);
    }

    public static <T> T readListJson(Context context, String fileName, Class clx) {
        Type type = getListType(clx);
        return readJson(context, fileName, type);
    }

    public static <T> T readJson(Context context, String fileName, Type clx) {
        String path = context.getCacheDir() + "/" + fileName + ".json";
        File file = new File(path);
        if (!file.exists())
            return null;
        Reader reader = null;
        try {
            reader = new FileReader(file);
            return AppOperator.getGson().fromJson(reader, clx);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            StreamUtil.close(reader);
        }
        return null;
    }

    private static Type getListType(Class clx) {
        return $Gson$Types.canonicalize((new ParameterizedTypeImpl(null, List.class, clx)));
    }

    private static final class ParameterizedTypeImpl implements ParameterizedType, Serializable {
        private final Type ownerType;
        private final Type rawType;
        private final Type[] typeArguments;

        public ParameterizedTypeImpl(Type ownerType, Type rawType, Type... typeArguments) {
            // require an owner type if the raw type needs it
            if (rawType instanceof Class<?>) {
                Class<?> rawTypeAsClass = (Class<?>) rawType;
                boolean isStaticOrTopLevelClass = Modifier.isStatic(rawTypeAsClass.getModifiers())
                        || rawTypeAsClass.getEnclosingClass() == null;
                checkArgument(ownerType != null || isStaticOrTopLevelClass);
            }

            this.ownerType = ownerType == null ? null : canonicalize(ownerType);
            this.rawType = canonicalize(rawType);
            this.typeArguments = typeArguments.clone();
            for (int t = 0; t < this.typeArguments.length; t++) {
                checkNotNull(this.typeArguments[t]);
                this.typeArguments[t] = canonicalize(this.typeArguments[t]);
            }
        }

        public Type[] getActualTypeArguments() {
            return typeArguments.clone();
        }

        public Type getRawType() {
            return rawType;
        }

        public Type getOwnerType() {
            return ownerType;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof ParameterizedType
                    && $Gson$Types.equals(this, (ParameterizedType) other);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(typeArguments)
                    ^ rawType.hashCode()
                    ^ (ownerType != null ? ownerType.hashCode() : 0);
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder(30 * (typeArguments.length + 1));
            stringBuilder.append(typeToString(rawType));

            if (typeArguments.length == 0) {
                return stringBuilder.toString();
            }

            stringBuilder.append("<").append(typeToString(typeArguments[0]));
            for (int i = 1; i < typeArguments.length; i++) {
                stringBuilder.append(", ").append(typeToString(typeArguments[i]));
            }
            return stringBuilder.append(">").toString();
        }

        private static final long serialVersionUID = 0;
    }
}
