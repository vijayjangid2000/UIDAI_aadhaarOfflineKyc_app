#include <jni.h>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_a2zsuvidhaa_in_MyApplication_HJJ29D(JNIEnv *env, jobject instance) {
    //return env->NewStringUTF("http://192.168.1.35/projects/A2Z/a2z_public/mobileapp/api/");
    //return env->NewStringUTF("https://partners.a2zsuvidhaa.com/mobileapp/api/");
    return env->NewStringUTF("https://prod.excelonestopsolution.com/mobileapp/api/");


}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_a2zsuvidhaa_in_MyApplication_TCCGHU(JNIEnv *env, jobject instance) {
    return env->NewStringUTF("userId");
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_a2zsuvidhaa_in_MyApplication_EE2JT(JNIEnv *env, jobject instance) {
    return env->NewStringUTF("token");
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_a2zsuvidhaa_in_MyApplication_FFEK(JNIEnv *env, jobject instance) {
    return env->NewStringUTF("ASKJHAU123SHYEWR");
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_a2zsuvidhaa_in_MyApplication_FFEP(JNIEnv *env, jobject instance) {
    return env->NewStringUTF("password");
}