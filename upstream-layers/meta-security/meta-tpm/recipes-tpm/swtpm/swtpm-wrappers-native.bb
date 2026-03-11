SUMMARY = "SWTPM - OpenEmbedded wrapper scripts for native swtpm tools"
LICENSE = "MIT"
DEPENDS = "swtpm-native"

inherit native

# The whole point of the recipe is to make files available
# for use after the build is done, so don't clean up...
RM_WORK_EXCLUDE += "${PN}"

do_create_wrapper () {
    # Wrap (almost) all swtpm binaries. Some get special wrappers and some
    # are not needed.
    for i in `find ${bindir} ${base_bindir} ${sbindir} ${base_sbindir} -name 'swtpm*' -perm /+x -type f`; do
        exe=`basename $i`
        case $exe in
            swtpm_setup)
                cat >${WORKDIR}/swtpm_setup_oe.sh <<EOF
#! /bin/sh
#
# Wrapper around swtpm_setup which adds parameters required to
# run the setup as non-root directly from the native sysroot.

PATH="${bindir}:${base_bindir}:${sbindir}:${base_sbindir}:\$PATH"
export PATH

exec swtpm_setup --config ${STAGING_DIR_NATIVE}/etc/swtpm_setup.conf "\$@"
EOF
                ;;
            *)
                cat >${WORKDIR}/${exe}_oe.sh <<EOF
#! /bin/sh
#
# Wrapper around $exe which makes it easier to invoke
# the right binary.

PATH="${bindir}:${base_bindir}:${sbindir}:${base_sbindir}:\$PATH"
export PATH

exec ${exe} "\$@"
EOF
                ;;
        esac
    done

    chmod a+rx ${WORKDIR}/*.sh
}

addtask do_create_wrapper before do_build after do_prepare_recipe_sysroot
