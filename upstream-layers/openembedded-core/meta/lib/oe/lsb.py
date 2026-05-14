#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

def get_os_release():
    """Get all key-value pairs from /etc/os-release as a dict"""

    data = {}
    if os.path.exists('/etc/os-release'):
        with open('/etc/os-release') as f:
            for line in f:
                try:
                    key, val = line.rstrip().split('=', 1)
                except ValueError:
                    continue
                data[key.strip()] = val.strip('"\'')
    return data

def distro_identifier(adjust_hook=None):
    """Return a distro identifier string based upon /etc/os-release
       with optional adjustment via a hook"""

    import re

    distro_data = get_os_release()

    distro_id = distro_data.get('ID')
    release = distro_data.get('VERSION_ID')

    if adjust_hook:
        distro_id, release = adjust_hook(distro_id, release)
    if not distro_id:
        return "unknown"
    # Filter out any non-alphanumerics and convert to lowercase
    distro_id = re.sub(r'\W', '', distro_id).lower()

    if release:
        id_str = '{0}-{1}'.format(distro_id, release)
    else:
        id_str = distro_id
    return id_str.replace(' ','-').replace('/','-')
