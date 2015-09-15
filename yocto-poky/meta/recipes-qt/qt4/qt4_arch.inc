inherit siteinfo

ARM_INSTRUCTION_SET = "arm"

set_arch() {
   case ${TARGET_ARCH} in
       aarch64*) QT_ARCH=aarch64 ;;
       arm*)     QT_ARCH=arm ;;
       i*86*)    QT_ARCH=i386 ;;
       mips*)    QT_ARCH=mips ;;
       powerpc*) QT_ARCH=powerpc ;;
       x86_64*)  QT_ARCH=x86_64 ;;
   esac
}

set_endian() {
    if [ ${SITEINFO_ENDIANNESS} = "le" ] ; then
        QT_ENDIAN="-little-endian"
    elif [ ${SITEINFO_ENDIANNESS} = "be" ] ; then
        QT_ENDIAN="-big-endian"
    fi
}
