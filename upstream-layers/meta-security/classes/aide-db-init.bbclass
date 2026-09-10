#
# Copyright 2022 Armin Kuster <akuster808@gmail.com>
#
# This class creates the initial aide database durning
# the build cycle allowing for that set being skipped during boot
# It has an additional benefit of having not being tamper with
# after build.
#
# To have the aide db created during build
# 1. Extend local.conf:
#    INHERIT += "adie-init-db"
#
# These are the defaults as defined in aide-base.bbclass
# They can be overriden in your local.conf or other distro include 
#
# To define where the share directory should be.
#    STAGING_AIDE_DIR = "${TMPDIR}/work-shared/${MACHINE}/aida"
#
# To define which directories should be inclued in a scan
#    AIDE_INCLUDE_DIRS ?= "/lib"
#
# To exclude directories and files from being scanned
#    AIDE_SKIP_DIRS ?= "/lib/modules/.\*"
#
# To controll if a db init should happen at postint 
#    AIDE_SCAN_POSTINIT ?= "0"
#
# To cotroll if a db recan should be run at postinit
#    AIDE_RESCAN_POSTINIT ?= "0"

inherit aide-base 

aide_init_db() {
    install -d ${STAGING_AIDE_DIR}/lib/logs
    rm -f ${STAGING_AIDE_DIR}/aide.conf ${STAGING_AIDE_DIR}/lib/aide.db ${STAGING_AIDE_DIR}/lib/aide.db.gz ${STAGING_AIDE_DIR}/lib/logs/aide.log
    install ${STAGING_DATADIR_NATIVE}/aide/aide.conf ${STAGING_AIDE_DIR}/

    sed -i -s "s:\@\@define DBDIR.*:\@\@define DBDIR ${STAGING_AIDE_DIR}/lib:" ${STAGING_AIDE_DIR}/aide.conf
    sed -i -e "s:\@\@define LOGDIR.*:\@\@define LOGDIR ${STAGING_AIDE_DIR}/lib/logs:" ${STAGING_AIDE_DIR}/aide.conf

    for dir in ${AIDE_INCLUDE_DIRS}; do
        echo "${IMAGE_ROOTFS}${dir} NORMAL" >> ${STAGING_AIDE_DIR}/aide.conf
    done
    for dir in ${AIDE_SKIP_DIRS}; do
        echo "!${IMAGE_ROOTFS}${dir}" >> ${STAGING_AIDE_DIR}/aide.conf
    done


    ${STAGING_BINDIR_NATIVE}/aide -c ${STAGING_AIDE_DIR}/aide.conf --init
    gunzip ${STAGING_AIDE_DIR}/lib/aide.db.gz 
    # strip out native path
    sed -i -e 's:${IMAGE_ROOTFS}::' ${STAGING_AIDE_DIR}/lib/aide.db
    gzip -9 ${STAGING_AIDE_DIR}/lib/aide.db 
    cp -f ${STAGING_AIDE_DIR}/lib/aide.db.gz ${IMAGE_ROOTFS}${libdir}/aide
}

do_rootfs[depends] += "aide-native:do_populate_sysroot"

ROOTFS_POSTPROCESS_COMMAND:append = " aide_init_db;"
