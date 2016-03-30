require recipes-devtools/gcc/gcc-${PV}.inc
require gcc-target.inc

# http://errors.yoctoproject.org/Errors/Details/20497/
ARM_INSTRUCTION_SET_armv4 = "arm"
ARM_INSTRUCTION_SET_armv5 = "arm"

BBCLASSEXTEND = "nativesdk"

#SYSTEMHEADERS_class-nativesdk = "${@'${target_includedir}'.replace(d.getVar('SDKPATH', True),'%r')}"
#SYSTEMLIBS_class-nativesdk = "${@'${target_base_libdir}'.replace(d.getVar('SDKPATH', True),'%r')}/"
#SYSTEMLIBS1_class-nativesdk = "${@'${target_libdir}'.replace(d.getVar('SDKPATH', True),'%r')}/"

