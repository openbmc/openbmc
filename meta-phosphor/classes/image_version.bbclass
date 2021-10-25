# Base image version class extension

DEPENDS:append = " os-release"

def do_get_version(d):
    import configparser
    import io
    path = d.getVar('STAGING_DIR_TARGET', True) + d.getVar('sysconfdir', True)
    path = os.path.join(path, 'os-release')
    parser = configparser.ConfigParser(strict=False)
    parser.optionxform = str
    version = ''
    try:
        with open(path, 'r') as fd:
            buf = '[root]\n' + fd.read()
            fd = io.StringIO(buf)
            parser.readfp(fd)
            version = parser['root']['VERSION_ID']
    except:
        pass
    return version

def do_get_versionID(d):
    import hashlib
    version = do_get_version(d)
    version = version.strip('"')
    version_id = (hashlib.sha512(version.encode('utf-8')).hexdigest())[:8]
    return version_id

def do_get_buildID(d):
    import configparser
    import io
    path = d.getVar('STAGING_DIR_TARGET', True) + d.getVar('sysconfdir', True)
    path = os.path.join(path, 'os-release')
    parser = configparser.ConfigParser(strict=False)
    parser.optionxform = str
    build_id = ''
    try:
        with open(path, 'r') as fd:
            buf = '[root]\n' + fd.read()
            fd = io.StringIO(buf)
            parser.readfp(fd)
            build_id = parser['root']['BUILD_ID']
    except:
        pass
    return build_id
