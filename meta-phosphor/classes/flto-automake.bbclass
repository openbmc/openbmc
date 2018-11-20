inherit autotools

# use gcc equivalents of AR and RANLIB
# to use -flto with shared libs
PACKAGECONFIG_CONFARGS += " AR=${TARGET_PREFIX}gcc-ar \
                            RANLIB=${TARGET_PREFIX}gcc-ranlib"
