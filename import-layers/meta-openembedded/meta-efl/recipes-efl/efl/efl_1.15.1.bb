require ${BPN}.inc

SRC_URI = "\
    ${E_RELEASES}/libs/${SRCNAME}/${SRCNAME}-${SRCVER}.tar.gz \
    file://0001-configure.ac-Don-t-check-for-Xprint-extension.patch \
    file://0001-evas_3d-Add-Eet.h-includes.patch \
"

SRC_URI[md5sum] = "dc91bcab73275f678e1748a2dea8d88a"
SRC_URI[sha256sum] = "2a6d0ab28020b4741be20b32ad0ffeb8f97f766c5c32175bacc2f2d0dc52f98c"

# Temporary disable until error like the one following are fixed
# efl-native/2_1.8.4-r0/efl-1.8.4/src/lib/eet/.libs/libeet.so: file not recognized: File truncated
# collect2: error: ld returned 1 exit status
# x86_64-linux-libtool: install: error: relink `modules/evas/savers/tiff/module.la' with the above command before installing it
# Makefile:11031: recipe for target 'install-savertiffpkgLTLIBRARIES' failed
PARALLEL_MAKE_class-native = ""

#EFL_RUN_IN_TREE=1 ../src/bin/eolian/eolian_gen --eo --legacy -I/OE/build/shr-core/tmp-eglibc/work/x86_64-linux/efl-native/2_1.10.0-r0/efl-1.10.0/src/lib/eo -I/OE/build/shr-core/tmp-eglibc/work/x86_64-linux/efl-native/2_1.10.0-r0/efl-1.10.0/src/lib/evas/canvas -I/OE/build/shr-core/tmp-eglibc/work/x86_64-linux/efl-native/2_1.10.0-r0/efl-1.10.0/src/lib/edje -I/OE/build/shr-core/tmp-eglibc/work/x86_64-linux/efl-native/2_1.10.0-r0/efl-1.10.0/src/lib/ecore_audio --gc -o lib/evas/canvas/evas_line.eo.c /OE/build/shr-core/tmp-eglibc/work/x86_64-linux/efl-native/2_1.10.0-r0/efl-1.10.0/src/lib/evas/canvas/evas_line.eo
#ERR<32439>:eolian_gen /OE/build/shr-core/tmp-eglibc/work/x86_64-linux/efl-native/2_1.10.0-r0/efl-1.10.0/src/bin/eolian/main.c:103 _generate_c_file() Couldnt open file lib/evas/canvas/evas_line.eo.c for writing
#Makefile:30738: recipe for target 'lib/evas/canvas/evas_line.eo.c' failed
#make[2]: *** [lib/evas/canvas/evas_line.eo.c] Error 1
#Makefile:2163: recipe for target 'all-recursive' failed
# autotools-brokensep
B = "${S}"

#ld: error: modules/evas/image_loaders/png/bin_evas_evas_cserve2_slave-evas_image_load_png.o: requires unsupported dynamic reloc R_ARM_MOVW_ABS_NC; recompile with -fPIC
#ld: error: modules/evas/image_loaders/png/bin_evas_evas_cserve2_slave-evas_image_load_png.o: requires unsupported dynamic reloc R_ARM_MOVW_ABS_NC; recompile with -fPIC
CFLAGS += "-fPIC"
