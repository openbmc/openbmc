SUMMARY = "GDAL is a translator library for raster geospatial data formats"
HOMEPAGE = "http://www.gdal.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.TXT;md5=0952e17969fab12227096b5228f23149"

DEPENDS = "proj sqlite3 tiff json-c"

SRC_URI = "ftp://download.osgeo.org/gdal/${PV}/${BP}.tar.xz"

SRC_URI[md5sum] = "2e126d7c6605691d38f3e71b945f5c73"
SRC_URI[sha256sum] = "20e1042cff15a71038459a299732fb342428aea9912f32df30c85790fcab6302"

inherit autotools-brokensep lib_package binconfig

EXTRA_OECONF = "--without-perl \
                --without-php \
                --without-ruby \
                --without-python \
                \
                --without-grass \
                --without-libgrass \
                --without-cfitsio \
                --without-dds \
                --without-gta \
                --without-pcidsk \
                --without-ogdi \
                --without-fme \
                --without-hdf4 \
                --without-hdf5 \
                --without-pg \
                --without-jpeg12 \
                --without-ogdi \
                --without-netcdf \
                --without-openjpeg \
                --without-fgdb \
                --without-ecw \
                --without-kakadu \
                --without-mrsid \
                --without-jp2mrsid \
                --without-mrsid_lidar \
                --without-msg \
                --without-bsb \
                --without-grib \
                --without-mysql \
                --without-ingres \
                --without-odbc \
                --without-dods_root \
                --without-xml2 \
                --without-spatialite \
                --without-pcre \
                --without-dwgdirect \
                --without-dwgdirect \
                --without-idb \
                --without-sde \
                --without-sde-version \
                --without-epsilon \
                --without-webp \
                --without-opencl \
                --without-opencl-include \
                --without-opencl-lib \
                --without-freexl \
                --without-pam \
                --without-poppler \
                --without-podofo \
                --without-podofo-lib \
                --without-podofo-extra-lib-for-test \
                --without-static_proj4 \
                --without-perl \
                --without-php \
                --without-ruby \
                --without-python \
                --without-java \
                --without-mdb \
                --without-jvm-lib \
                --without-jvm-lib-add-rpath \
                --without-rasdaman \
                --without-armadillo \
                \
                --with-pcraster=internal \
                --with-geotiff=internal \
                \
                --with-sqlite3=${STAGING_EXECPREFIXDIR} \
                --with-libtiff=${STAGING_EXECPREFIXDIR} \
                --with-libjson-c=${STAGING_EXECPREFIXDIR} \
                --with-expat=${STAGING_EXECPREFIXDIR} \
"

EXTRA_OEMAKE += "INST_DATA="${datadir}/gdal""

PACKAGECONFIG ?= "geos png jasper"
PACKAGECONFIG[geos] = "--with-geos,--without-geos,geos"
PACKAGECONFIG[lzma] = "--with-liblzma,--without-liblzma,xz"
PACKAGECONFIG[png] = "--with-png,--without-png,libpng"
PACKAGECONFIG[gif] = "--with-gif,--without-gif,giflib"
PACKAGECONFIG[jpeg] = "--with-jpeg,--without-jpeg,jpeg"
PACKAGECONFIG[z] = "--with-libz,--without-libz,zlib"
PACKAGECONFIG[jasper] = "--with-jasper,--without-jasper,jasper"
PACKAGECONFIG[curl] = "--with-curl,--without-curl,curl"

do_configure_prepend () {
    # The configure script has many hardcoded paths to search
    # for the library headers when using external libraries,
    # workaround it.
    sed -e 's,/usr/include,NON_EXISTENT_DIR,g' \
        -e 's,/usr/lib,NON_EXISTENT_DIR,g' \
        -i ${S}/configure.in
}

FILES_${PN} += "${libdir}/gdalplugins"

# | gdalserver.c:124:21: error: storage size of 'sHints' isn't known
# |      struct addrinfo sHints;
# |                      ^
PNBLACKLIST[gdal] ?= "BROKEN: fails to build with gcc-5"
