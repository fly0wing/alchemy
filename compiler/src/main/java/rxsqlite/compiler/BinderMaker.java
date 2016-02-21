package rxsqlite.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import javax.lang.model.element.Modifier;

/**
 * @author Daniel Serdyukov
 */
class BinderMaker {

    private static final String CLASS_NAME = "RxSQLiteBinderWrapper";

    private static final TypeVariableName T_VAR = TypeVariableName.get("T");

    static ClassName className() {
        return ClassName.get(TableMaker.PACKAGE_NAME, CLASS_NAME);
    }

    static JavaFile brewJava() {
        final TypeSpec.Builder spec = TypeSpec.classBuilder(CLASS_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addField(TableMaker.RXS_BINDER, "mBinder", Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(MethodSpec.constructorBuilder()
                        .addParameter(TableMaker.RXS_BINDER, "binder")
                        .addStatement("mBinder = binder")
                        .build());
        brewBindValueMethod(spec);
        brewGetValueMethod(spec);
        brewGetEnumValueMethod(spec);
        return JavaFile.builder(TableMaker.PACKAGE_NAME, spec.build())
                .addFileComment("Generated code from RxSQLite. Do not modify!")
                .skipJavaLangImports(true)
                .build();
    }

    private static void brewBindValueMethod(TypeSpec.Builder typeSpec) {
        typeSpec.addMethod(MethodSpec.methodBuilder("bindValue")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TableMaker.SQLITE_STMT, "stmt")
                .addParameter(TypeName.INT, "index")
                .addParameter(ClassName.get(Object.class), "object")
                .addStatement("mBinder.bindValue(stmt, index, object)")
                .build());
    }

    private static void brewGetValueMethod(TypeSpec.Builder typeSpec) {
        typeSpec.addMethod(MethodSpec.methodBuilder("getValue")
                .addModifiers(Modifier.PUBLIC)
                .addTypeVariable(T_VAR)
                .addParameter(TableMaker.SQLITE_ROW, "row")
                .addParameter(TypeName.INT, "index")
                .addParameter(ParameterizedTypeName.get(ClassName.get(Class.class), T_VAR), "type")
                .returns(T_VAR)
                .addStatement("return mBinder.getValue(row, index, type)")
                .build());
    }

    private static void brewGetEnumValueMethod(TypeSpec.Builder typeSpec) {
        typeSpec.addMethod(MethodSpec.methodBuilder("getEnumValue")
                .addModifiers(Modifier.PUBLIC)
                .addTypeVariable(TypeVariableName.get("T", ParameterizedTypeName
                        .get(ClassName.get(Enum.class), T_VAR)))
                .addParameter(TableMaker.SQLITE_ROW, "row")
                .addParameter(TypeName.INT, "index")
                .addParameter(ParameterizedTypeName.get(ClassName.get(Class.class), T_VAR), "type")
                .returns(T_VAR)
                .addStatement("return mBinder.getEnumValue(row, index, type)")
                .build());
    }

}
