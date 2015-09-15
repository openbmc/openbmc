def glibc_cfg(feature, tokens, cnf):
    if type(tokens) == type(""):
        tokens = [tokens]
    if feature:
        cnf.extend([token + '=y' for token in tokens])
    else:
        for token in tokens:
            cnf.extend([token + '=n'])
            if token == 'OPTION_EGLIBC_NSSWITCH':
                cnf.extend(["OPTION_EGLIBC_NSSWITCH_FIXED_CONFIG=\"${S}/nss/nsswitch.conf\""])
                cnf.extend(["OPTION_EGLIBC_NSSWITCH_FIXED_FUNCTIONS=\"${S}/nss/fixed-nsswitch.functions\""])

# Map distro features to glibc options settings
def features_to_glibc_settings(d):
    cnf = ([])

    ipv4 = bb.utils.contains('DISTRO_FEATURES', 'ipv4', True, False, d)
    ipv6 = bb.utils.contains('DISTRO_FEATURES', 'ipv6', True, False, d)
    libc_backtrace = bb.utils.contains('DISTRO_FEATURES', 'libc-backtrace', True, False, d) 
    libc_big_macros = bb.utils.contains('DISTRO_FEATURES', 'libc-big-macros', True, False, d)
    libc_bsd = bb.utils.contains('DISTRO_FEATURES', 'libc-bsd', True, False, d)
    libc_cxx_tests = bb.utils.contains('DISTRO_FEATURES', 'libc-cxx-tests', True, False, d)
    libc_catgets = bb.utils.contains('DISTRO_FEATURES', 'libc-catgets', True, False, d)
    libc_charsets = bb.utils.contains('DISTRO_FEATURES', 'libc-charsets', True, False, d)
    libc_crypt = bb.utils.contains('DISTRO_FEATURES', 'libc-crypt', True, False, d)
    libc_crypt_ufc = bb.utils.contains('DISTRO_FEATURES', 'libc-crypt-ufc', True, False, d)
    libc_db_aliases = bb.utils.contains('DISTRO_FEATURES', 'libc-db-aliases', True, False, d)
    libc_envz = bb.utils.contains('DISTRO_FEATURES', 'libc-envz', True, False, d)
    libc_fcvt = bb.utils.contains('DISTRO_FEATURES', 'libc-fcvt', True, False, d)
    libc_fmtmsg = bb.utils.contains('DISTRO_FEATURES', 'libc-fmtmsg', True, False, d)
    libc_fstab = bb.utils.contains('DISTRO_FEATURES', 'libc-fstab', True, False, d)
    libc_ftraverse = bb.utils.contains('DISTRO_FEATURES', 'libc-ftraverse', True, False, d)
    libc_getlogin = bb.utils.contains('DISTRO_FEATURES', 'libc-getlogin', True, False, d)
    libc_idn = bb.utils.contains('DISTRO_FEATURES', 'libc-idn', True, False, d)
    libc_inet_anl = bb.utils.contains('DISTRO_FEATURES', 'libc-inet-anl', True, False, d)
    libc_libm = bb.utils.contains('DISTRO_FEATURES', 'libc-libm', True, False, d)
    libc_locales = bb.utils.contains('DISTRO_FEATURES', 'libc-locales', True, False, d)
    libc_locale_code = bb.utils.contains('DISTRO_FEATURES', 'libc-locale-code', True, False, d)
    libc_memusage = bb.utils.contains('DISTRO_FEATURES', 'libc-memusage', True, False, d)
    libc_nis = bb.utils.contains('DISTRO_FEATURES', 'libc-nis', True, False, d)
    libc_nsswitch = bb.utils.contains('DISTRO_FEATURES', 'libc-nsswitch', True, False, d)
    libc_rcmd = bb.utils.contains('DISTRO_FEATURES', 'libc-rcmd', True, False, d)
    libc_rtld_debug = bb.utils.contains('DISTRO_FEATURES', 'libc-rtld-debug', True, False, d)
    libc_spawn = bb.utils.contains('DISTRO_FEATURES', 'libc-spawn', True, False, d)
    libc_streams = bb.utils.contains('DISTRO_FEATURES', 'libc-streams', True, False, d)
    libc_sunrpc = bb.utils.contains('DISTRO_FEATURES', 'libc-sunrpc', True, False, d)
    libc_utmp = bb.utils.contains('DISTRO_FEATURES', 'libc-utmp', True, False, d)
    libc_utmpx = bb.utils.contains('DISTRO_FEATURES', 'libc-utmpx', True, False, d)
    libc_wordexp = bb.utils.contains('DISTRO_FEATURES', 'libc-wordexp', True, False, d)
    libc_posix_clang_wchar = bb.utils.contains('DISTRO_FEATURES', 'libc-posix-clang-wchar', True, False, d)
    libc_posix_regexp = bb.utils.contains('DISTRO_FEATURES', 'libc-posix-regexp', True, False, d)
    libc_posix_regexp_glibc = bb.utils.contains('DISTRO_FEATURES', 'libc-posix-regexp-glibc', True, False, d)
    libc_posix_wchar_io = bb.utils.contains('DISTRO_FEATURES', 'libc-posix-wchar-io', True, False, d)

    # arrange the dependencies among glibc configuable options according to file option-groups.def from glibc source code
    new_dep = True
    while new_dep:
        new_dep = False

        if ipv6 and not ipv4:
            new_dep = True
            ipv4 = True

        if ipv4 and not libc_nsswitch:
            new_dep = True
            libc_nsswitch = True

        if libc_cxx_tests:
            if not libc_posix_wchar_io:
                new_dep = True
                libc_posix_wchar_io = True
            if not libc_libm:
                new_dep = True
                libc_libm = True

        if libc_catgets and not libc_locale_code:
            new_dep = True
            libc_locale_code = True

        if libc_crypt_ufc and not libc_crypt:
            new_dep = True
            libc_crypt = True

        if libc_getlogin and not libc_utmp:
            new_dep = True
            libc_utmp = True

        if libc_inet_anl and not ipv4:
            new_dep = True
            ipv4 = True

        if libc_locale_code and not libc_posix_clang_wchar:
            new_dep = True
            libc_posix_clang_wchar = True

        if libc_nis:
            if not ipv4:
                new_dep = True
                ipv4 = True
            if not libc_sunrpc:
                new_dep = True
                libc_sunrpc = True

        if libc_rcmd and not ipv4:
            new_dep = True
            ipv4 = True

        if libc_sunrpc and not ipv4:
            new_dep = True
            ipv4 = True

        if libc_utmpx and not libc_utmp:
            new_dep = True
            libc_utmp = True

        if libc_posix_regexp_glibc and not libc_posix_regexp:
            new_dep = True
            libc_posix_regexp = True

        if libc_posix_wchar_io and not libc_posix_clang_wchar:
            new_dep = True
            libc_posix_clang_wchar = True

    glibc_cfg(ipv6, 'OPTION_EGLIBC_ADVANCED_INET6', cnf)
    glibc_cfg(libc_backtrace, 'OPTION_EGLIBC_BACKTRACE', cnf)
    glibc_cfg(libc_big_macros, 'OPTION_EGLIBC_BIG_MACROS', cnf)
    glibc_cfg(libc_bsd, 'OPTION_EGLIBC_BSD', cnf)
    glibc_cfg(libc_cxx_tests, 'OPTION_EGLIBC_CXX_TESTS', cnf)
    glibc_cfg(libc_catgets, 'OPTION_EGLIBC_CATGETS', cnf)
    glibc_cfg(libc_charsets, 'OPTION_EGLIBC_CHARSETS', cnf)
    glibc_cfg(libc_crypt, 'OPTION_EGLIBC_CRYPT', cnf)
    glibc_cfg(libc_crypt_ufc, 'OPTION_EGLIBC_CRYPT_UFC', cnf)
    glibc_cfg(libc_db_aliases, 'OPTION_EGLIBC_DB_ALIASES', cnf)
    glibc_cfg(libc_envz, 'OPTION_EGLIBC_ENVZ', cnf)
    glibc_cfg(libc_fcvt, 'OPTION_EGLIBC_FCVT', cnf)
    glibc_cfg(libc_fmtmsg, 'OPTION_EGLIBC_FMTMSG', cnf)
    glibc_cfg(libc_fstab, 'OPTION_EGLIBC_FSTAB', cnf)
    glibc_cfg(libc_ftraverse, 'OPTION_EGLIBC_FTRAVERSE', cnf)
    glibc_cfg(libc_getlogin, 'OPTION_EGLIBC_GETLOGIN', cnf)
    glibc_cfg(libc_idn, 'OPTION_EGLIBC_IDN', cnf)
    glibc_cfg(ipv4, 'OPTION_EGLIBC_INET', cnf)
    glibc_cfg(libc_inet_anl, 'OPTION_EGLIBC_INET_ANL', cnf)
    glibc_cfg(libc_libm, 'OPTION_EGLIBC_LIBM', cnf)
    glibc_cfg(libc_locales, 'OPTION_EGLIBC_LOCALES', cnf)
    glibc_cfg(libc_locale_code, 'OPTION_EGLIBC_LOCALE_CODE', cnf)
    glibc_cfg(libc_memusage, 'OPTION_EGLIBC_MEMUSAGE', cnf)
    glibc_cfg(libc_nis, 'OPTION_EGLIBC_NIS', cnf)
    glibc_cfg(libc_nsswitch, 'OPTION_EGLIBC_NSSWITCH', cnf)
    glibc_cfg(libc_rcmd, 'OPTION_EGLIBC_RCMD', cnf)
    glibc_cfg(libc_rtld_debug, 'OPTION_EGLIBC_RTLD_DEBUG', cnf)
    glibc_cfg(libc_spawn, 'OPTION_EGLIBC_SPAWN', cnf)
    glibc_cfg(libc_streams, 'OPTION_EGLIBC_STREAMS', cnf)
    glibc_cfg(libc_sunrpc, 'OPTION_EGLIBC_SUNRPC', cnf)
    glibc_cfg(libc_utmp, 'OPTION_EGLIBC_UTMP', cnf)
    glibc_cfg(libc_utmpx, 'OPTION_EGLIBC_UTMPX', cnf)
    glibc_cfg(libc_wordexp, 'OPTION_EGLIBC_WORDEXP', cnf)
    glibc_cfg(libc_posix_clang_wchar, 'OPTION_POSIX_C_LANG_WIDE_CHAR', cnf)
    glibc_cfg(libc_posix_regexp, 'OPTION_POSIX_REGEXP', cnf)
    glibc_cfg(libc_posix_regexp_glibc, 'OPTION_POSIX_REGEXP_GLIBC', cnf)
    glibc_cfg(libc_posix_wchar_io, 'OPTION_POSIX_WIDE_CHAR_DEVICE_IO', cnf)

    return "\n".join(cnf)
