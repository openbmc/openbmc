DESCRIPTION = "library to compute convex hulls, Delaunay triangulations and Voronoi diagrams."
HOMEPAGE = "http://www.qhull.org/"
SECTION = "libs"
LICENSE = "Qhull"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=484ee0cacf0472e8b40225b116f6296c"

SRC_URI = "http://www.qhull.org/download/qhull-2020-src-8.0.2.tgz \
    file://0001-Use-LIB_INSTALL_DIR-for-cmake-and-pkgconfig-files-to.patch \
    file://0002-allow-build-with-cmake-4.patch \
"
SRC_URI[sha256sum] = "b5c2d7eb833278881b952c8a52d20179eab87766b00b865000469a45c1838b7e"

CFLAGS += "-fPIC"

EXTRA_OECMAKE += "\
    -DCMAKE_SKIP_RPATH=ON \
    -DLIB_INSTALL_DIR=${baselib} \
"

inherit cmake

# The QhullTargets-noconfig.cmake checks for the executables despite not
# needing to execute them for the build.  Staging bindir to the sysroot
# allows us to pass the check without building qhull natively
SYSROOT_DIRS:append = " \
    ${bindir} \
"

BBCLASSEXTEND = "native"
