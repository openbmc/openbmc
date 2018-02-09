# Base image version class extension

DEPENDS_append = " os-release"

def do_get_version(d):
    import configparser
    import io
    path = d.getVar('STAGING_DIR_TARGET', True) + d.getVar('sysconfdir', True)
    path = os.path.join(path, 'os-release')
    parser = configparser.SafeConfigParser(strict=False)
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
