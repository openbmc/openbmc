DESCRIPTION = "libgee is a collection library providing GObject-based interfaces \
and classes for commonly used data structures."
HOMEPAGE = "http://live.gnome.org/Libgee"
SECTION = "libs"
DEPENDS = "glib-2.0"

BBCLASSEXTEND = "native"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"

GNOMEBASEBUILDCLASS = "autotools"
inherit gnomebase vala gobject-introspection

CFLAGS += "-Wno-incompatible-pointer-types"

do_configure:prepend() {
    MACROS="libtool.m4 lt~obsolete.m4 ltoptions.m4 ltsugar.m4 ltversion.m4"
    for i in ${MACROS}; do
        rm -f m4/$i
    done
}

SRC_URI[archive.sha256sum] = "1bf834f5e10d60cc6124d74ed3c1dd38da646787fbf7872220b8b4068e476d4d"

# http://errors.yoctoproject.org/Errors/Details/766884/
# libgee-0.20.6/gee/concurrentlist.c:1169:177: error: passing argument 4 of 'gee_hazard_pointer_get_pointer' from incompatible pointer type [-Wincompatible-pointer-types]
# libgee-0.20.6/gee/concurrentlist.c:1175:168: error: passing argument 4 of 'gee_hazard_pointer_set_pointer' from incompatible pointer type [-Wincompatible-pointer-types]
# libgee-0.20.6/gee/concurrentlist.c:2385:194: error: passing argument 4 of 'gee_hazard_pointer_compare_and_exchange_pointer' from incompatible pointer type [-Wincompatible-pointer-types]
# libgee-0.20.6/gee/concurrentlist.c:2438:177: error: passing argument 4 of 'gee_hazard_pointer_get_pointer' from incompatible pointer type [-Wincompatible-pointer-types]
# libgee-0.20.6/gee/concurrentlist.c:2460:168: error: passing argument 4 of 'gee_hazard_pointer_set_pointer' from incompatible pointer type [-Wincompatible-pointer-types]
# libgee-0.20.6/gee/concurrentlist.c:2469:177: error: passing argument 4 of 'gee_hazard_pointer_get_pointer' from incompatible pointer type [-Wincompatible-pointer-types]
# libgee-0.20.6/gee/concurrentlist.c:2481:185: error: passing argument 4 of 'gee_hazard_pointer_compare_and_exchange_pointer' from incompatible pointer type [-Wincompatible-pointer-types]
# libgee-0.20.6/gee/concurrentlist.c:2640:168: error: passing argument 4 of 'gee_hazard_pointer_set_pointer' from incompatible pointer type [-Wincompatible-pointer-types]
# libgee-0.20.6/gee/concurrentlist.c:2641:168: error: passing argument 4 of 'gee_hazard_pointer_set_pointer' from incompatible pointer type [-Wincompatible-pointer-types]
# libgee-0.20.6/gee/concurrentlist.c:2750:168: error: passing argument 4 of 'gee_hazard_pointer_set_pointer' from incompatible pointer type [-Wincompatible-pointer-types]
# libgee-0.20.6/gee/concurrentlist.c:469:168: error: passing argument 4 of 'gee_hazard_pointer_set_pointer' from incompatible pointer type [-Wincompatible-pointer-types]
# libgee-0.20.6/gee/concurrentlist.c:514:168: error: passing argument 4 of 'gee_hazard_pointer_set_pointer' from incompatible pointer type [-Wincompatible-pointer-types]
# libgee-0.20.6/gee/concurrentlist.c:713:168: error: passing argument 4 of 'gee_hazard_pointer_set_pointer' from incompatible pointer type [-Wincompatible-pointer-types]
# libgee-0.20.6/gee/concurrentset.c:3635:185: error: passing argument 4 of 'gee_hazard_pointer_get_pointer' from incompatible pointer type [-Wincompatible-pointer-types]
# libgee-0.20.6/gee/concurrentset.c:4950:201: error: passing argument 4 of 'gee_hazard_pointer_get_pointer' from incompatible pointer type [-Wincompatible-pointer-types]
# libgee-0.20.6/gee/concurrentset.c:5378:201: error: passing argument 4 of 'gee_hazard_pointer_get_pointer' from incompatible pointer type [-Wincompatible-pointer-types]
# libgee-0.20.6/gee/concurrentset.c:5428:226: error: passing argument 4 of 'gee_hazard_pointer_get_pointer' from incompatible pointer type [-Wincompatible-pointer-types]
# libgee-0.20.6/gee/concurrentset.c:5480:218: error: passing argument 4 of 'gee_hazard_pointer_get_pointer' from incompatible pointer type [-Wincompatible-pointer-types]
# libgee-0.20.6/gee/concurrentset.c:5530:238: error: passing argument 4 of 'gee_hazard_pointer_compare_and_exchange_pointer' from incompatible pointer type [-Wincompatible-pointer-types]
# libgee-0.20.6/gee/concurrentset.c:5532:234: error: passing argument 4 of 'gee_hazard_pointer_get_pointer' from incompatible pointer type [-Wincompatible-pointer-types]
# libgee-0.20.6/gee/concurrentset.c:5623:246: error: passing argument 4 of 'gee_hazard_pointer_compare_and_exchange_pointer' from incompatible pointer type [-Wincompatible-pointer-types]
# libgee-0.20.6/gee/concurrentset.c:5625:242: error: passing argument 4 of 'gee_hazard_pointer_get_pointer' from incompatible pointer type [-Wincompatible-pointer-types]
# libgee-0.20.6/gee/concurrentset.c:7088:194: error: passing argument 4 of 'gee_hazard_pointer_compare_and_exchange_pointer' from incompatible pointer type [-Wincompatible-pointer-types]
# libgee-0.20.6/gee/concurrentset.c:7157:177: error: passing argument 4 of 'gee_hazard_pointer_get_pointer' from incompatible pointer type [-Wincompatible-pointer-types]
# libgee-0.20.6/gee/concurrentset.c:7184:168: error: passing argument 4 of 'gee_hazard_pointer_set_pointer' from incompatible pointer type [-Wincompatible-pointer-types]
# libgee-0.20.6/gee/concurrentset.c:7198:177: error: passing argument 4 of 'gee_hazard_pointer_get_pointer' from incompatible pointer type [-Wincompatible-pointer-types]
# libgee-0.20.6/gee/concurrentset.c:7215:185: error: passing argument 4 of 'gee_hazard_pointer_compare_and_exchange_pointer' from incompatible pointer type [-Wincompatible-pointer-types]
# libgee-0.20.6/gee/hashmap.c:4089:30: error: assignment to 'gboolean (*)(GeeMapIterator *)' {aka 'int (*)(struct _GeeMapIterator *)'} from incompatible pointer type 'gboolean (*)(GeeHashMapNodeIterator *)' {aka 'int (*)(struct _GeeHashMapNodeIterator *)'} [-Wincompatible-pointer-types]
# libgee-0.20.6/gee/hazardpointer.c:430:134: error: passing argument 4 of 'gee_hazard_pointer_release_policy_swap' from incompatible pointer type [-Wincompatible-pointer-types]
# libgee-0.20.6/gee/hazardpointer.c:430:171: error: passing argument 5 of 'gee_hazard_pointer_release_policy_swap' from incompatible pointer type [-Wincompatible-pointer-types]
# libgee-0.20.6/gee/hazardpointer.c:434:134: error: passing argument 4 of 'gee_hazard_pointer_release_policy_swap' from incompatible pointer type [-Wincompatible-pointer-types]
# libgee-0.20.6/gee/hazardpointer.c:434:171: error: passing argument 5 of 'gee_hazard_pointer_release_policy_swap' from incompatible pointer type [-Wincompatible-pointer-types]
CFLAGS += "-Wno-error=incompatible-pointer-types"
