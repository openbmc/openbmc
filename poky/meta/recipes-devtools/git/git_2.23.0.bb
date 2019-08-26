require git.inc

EXTRA_OECONF += "ac_cv_snprintf_returns_bogus=no \
                 ac_cv_fread_reads_directories=${ac_cv_fread_reads_directories=yes} \
                 "
EXTRA_OEMAKE += "NO_GETTEXT=1"

SRC_URI[tarball.md5sum] = "203c238ffdcef76f9bd6c67cfbaf949f"
SRC_URI[tarball.sha256sum] = "e3396c90888111a01bf607346db09b0fbf49a95bc83faf9506b61195936f0cfe"
SRC_URI[manpages.md5sum] = "90a72e553de712d798d68b15b57bc928"
SRC_URI[manpages.sha256sum] = "a5b0998f95c2290386d191d34780d145ea67e527fac98541e0350749bf76be75"
