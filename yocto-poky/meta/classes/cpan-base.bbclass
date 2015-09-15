#
# cpan-base providers various perl related information needed for building
# cpan modules
#
FILES_${PN} += "${libdir}/perl ${datadir}/perl"

DEPENDS  += "${@["perl", "perl-native"][(bb.data.inherits_class('native', d))]}"
RDEPENDS_${PN} += "${@["perl", ""][(bb.data.inherits_class('native', d))]}"

PERL_OWN_DIR = "${@["", "/perl-native"][(bb.data.inherits_class('native', d))]}"

# Determine the staged version of perl from the perl configuration file
# Assign vardepvalue, because otherwise signature is changed before and after
# perl is built (from None to real version in config.sh).
get_perl_version[vardepvalue] = "${PERL_OWN_DIR}"
def get_perl_version(d):
    import re
    cfg = d.expand('${STAGING_LIBDIR}${PERL_OWN_DIR}/perl/config.sh')
    try:
        f = open(cfg, 'r')
    except IOError:
        return None
    l = f.readlines();
    f.close();
    r = re.compile("^version='(\d*\.\d*\.\d*)'")
    for s in l:
        m = r.match(s)
        if m:
            return m.group(1)
    return None

# Determine where the library directories are
def perl_get_libdirs(d):
    libdir = d.getVar('libdir', True)
    if is_target(d) == "no":
        libdir += '/perl-native'
    libdir += '/perl'
    return libdir

def is_target(d):
    if not bb.data.inherits_class('native', d):
        return "yes"
    return "no"

PERLLIBDIRS := "${@perl_get_libdirs(d)}"
PERLVERSION := "${@get_perl_version(d)}"
PERLVERSION[vardepvalue] = ""

FILES_${PN}-dbg += "${PERLLIBDIRS}/auto/*/.debug \
                    ${PERLLIBDIRS}/auto/*/*/.debug \
                    ${PERLLIBDIRS}/auto/*/*/*/.debug \
                    ${PERLLIBDIRS}/vendor_perl/${PERLVERSION}/auto/*/.debug \
                    ${PERLLIBDIRS}/vendor_perl/${PERLVERSION}/auto/*/*/.debug \
                    ${PERLLIBDIRS}/vendor_perl/${PERLVERSION}/auto/*/*/*/.debug \
                    "
