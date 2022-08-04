# Base image version class extension

DEPENDS:append = " os-release"

def do_get_os_release_value(d, key):
    import configparser
    import io
    path = d.getVar('STAGING_DIR_TARGET', True) + d.getVar('sysconfdir', True)
    path = os.path.join(path, 'os-release')
    parser = configparser.ConfigParser(strict=False)
    parser.optionxform = str
    value = ''
    try:
        with open(path, 'r') as fd:
            buf = '[root]\n' + fd.read()
            fd = io.StringIO(buf)
            parser.readfp(fd)
            value = parser['root'][key]
    except:
        pass
    return value

def do_get_version(d):
    version = do_get_os_release_value(d, 'VERSION_ID')
    return version

def do_get_versionID(d):
    import hashlib
    version = do_get_version(d)
    version = version.strip('"')
    version_id = (hashlib.sha512(version.encode('utf-8')).hexdigest())[:8]
    return version_id

def do_get_buildID(d):
    build_id = do_get_os_release_value(d, 'BUILD_ID')
    return build_id

def do_get_extended_version(d):
    extended_version = do_get_os_release_value(d, 'EXTENDED_VERSION')
    return extended_version
