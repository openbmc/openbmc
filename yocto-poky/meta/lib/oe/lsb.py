def release_dict():
    """Return the output of lsb_release -ir as a dictionary"""
    from subprocess import PIPE

    try:
        output, err = bb.process.run(['lsb_release', '-ir'], stderr=PIPE)
    except bb.process.CmdError as exc:
        return None

    data = {}
    for line in output.splitlines():
        if line.startswith("-e"): line = line[3:]
        try:
            key, value = line.split(":\t", 1)
        except ValueError:
            continue
        else:
            data[key] = value
    return data

def release_dict_file():
    """ Try to gather LSB release information manually when lsb_release tool is unavailable """
    data = None
    try:
        if os.path.exists('/etc/lsb-release'):
            data = {}
            with open('/etc/lsb-release') as f:
                for line in f:
                    key, value = line.split("=", 1)
                    data[key] = value.strip()
        elif os.path.exists('/etc/redhat-release'):
            data = {}
            with open('/etc/redhat-release') as f:
                distro = f.readline().strip()
            import re
            match = re.match(r'(.*) release (.*) \((.*)\)', distro)
            if match:
                data['DISTRIB_ID'] = match.group(1)
                data['DISTRIB_RELEASE'] = match.group(2)
        elif os.path.exists('/etc/os-release'):
            data = {}
            with open('/etc/os-release') as f:
                for line in f:
                    if line.startswith('NAME='):
                        data['DISTRIB_ID'] = line[5:].rstrip().strip('"')
                    if line.startswith('VERSION_ID='):
                        data['DISTRIB_RELEASE'] = line[11:].rstrip().strip('"')
        elif os.path.exists('/etc/SuSE-release'):
            data = {}
            data['DISTRIB_ID'] = 'SUSE LINUX'
            with open('/etc/SuSE-release') as f:
                for line in f:
                    if line.startswith('VERSION = '):
                        data['DISTRIB_RELEASE'] = line[10:].rstrip()
                        break

    except IOError:
        return None
    return data

def distro_identifier(adjust_hook=None):
    """Return a distro identifier string based upon lsb_release -ri,
       with optional adjustment via a hook"""

    import re

    lsb_data = release_dict()
    if lsb_data:
        distro_id, release = lsb_data['Distributor ID'], lsb_data['Release']
    else:
        lsb_data_file = release_dict_file()
        if lsb_data_file:
            distro_id, release = lsb_data_file['DISTRIB_ID'], lsb_data_file.get('DISTRIB_RELEASE', None)
        else:
            distro_id, release = None, None

    if adjust_hook:
        distro_id, release = adjust_hook(distro_id, release)
    if not distro_id:
        return "Unknown"
    # Filter out any non-alphanumerics
    distro_id = re.sub(r'\W', '', distro_id)

    if release:
        id_str = '{0}-{1}'.format(distro_id, release)
    else:
        id_str = distro_id
    return id_str.replace(' ','-').replace('/','-')
