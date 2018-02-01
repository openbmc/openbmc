def ld_append_if_tune_exists(d, infos, dict):
    tune = d.getVar("DEFAULTTUNE") or ""
    libdir = d.getVar("base_libdir") or ""
    if tune in dict:
        infos['ldconfig'].add('{"' + libdir + '/' + dict[tune][0] + '",' + dict[tune][1] + ' }')
        infos['lddrewrite'].add(libdir+'/'+dict[tune][0])

def glibc_dl_info(d):
    ld_info_all = {
        "mipsarch": ["ld.so.1", "FLAG_ELF_LIBC6"],
        "mipsarchr6": ["ld-linux-mipsn8.so.1", "FLAG_ELF_LIBC6"],
        "powerpc": ["ld.so.1", "FLAG_ELF_LIBC6"],
        "powerpc-nf": ["ld.so.1", "FLAG_ELF_LIBC6"],
        "powerpc64": ["ld64.so.1", "FLAG_ELF_LIBC6"],
        "powerpc64-nf": ["ld64.so.1", "FLAG_ELF_LIBC6"],
        "core2-32": ["ld-linux.so.2", "FLAG_ELF_LIBC6"],
        "core2-64": ["ld-linux-x86-64.so.2", "FLAG_ELF_LIBC6"],
        "x86": ["ld-linux.so.2", "FLAG_ELF_LIBC6"],
        "x86-64": ["ld-linux-x86-64.so.2", "FLAG_ELF_LIBC6"],
        "i586": ["ld-linux.so.2", "FLAG_ELF_LIBC6"],
        "corei7-32": ["ld-linux.so.2", "FLAG_ELF_LIBC6"],
        "corei7-64": ["ld-linux-x86-64.so.2", "FLAG_ELF_LIBC6"],
    }

    infos = {'ldconfig':set(), 'lddrewrite':set()}
    ld_append_if_tune_exists(d, infos, ld_info_all)

    #DEFAULTTUNE_MULTILIB_ORIGINAL
    original_tune=d.getVar("DEFAULTTUNE_MULTILIB_ORIGINAL")
    if original_tune:
        localdata = bb.data.createCopy(d)
        localdata.setVar("DEFAULTTUNE", original_tune)
        ld_append_if_tune_exists(localdata, infos, ld_info_all)

    variants = d.getVar("MULTILIB_VARIANTS") or ""
    for item in variants.split():
        localdata = bb.data.createCopy(d)
        overrides = localdata.getVar("OVERRIDES", False) + ":virtclass-multilib-" + item
        localdata.setVar("OVERRIDES", overrides)
        ld_append_if_tune_exists(localdata, infos, ld_info_all)
    infos['ldconfig'] = ','.join(infos['ldconfig'])
    infos['lddrewrite'] = ' '.join(infos['lddrewrite'])
    return infos

EGLIBC_KNOWN_INTERPRETER_NAMES = "${@glibc_dl_info(d)['ldconfig']}"
RTLDLIST = "${@glibc_dl_info(d)['lddrewrite']}"
RTLDLIST_class-nativesdk = "${base_libdir}/${@bb.utils.contains('SDK_ARCH', 'x86_64', 'ld-linux-x86-64.so.2', 'ld-linux.so.2', d)}"
glibc_dl_info[vardepsexclude] = "OVERRIDES"
