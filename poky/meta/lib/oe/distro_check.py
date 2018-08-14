def create_socket(url, d):
    import urllib
    from bb.utils import export_proxies

    export_proxies(d)
    return urllib.request.urlopen(url)

def get_links_from_url(url, d):
    "Return all the href links found on the web location"

    from bs4 import BeautifulSoup, SoupStrainer

    soup = BeautifulSoup(create_socket(url,d), "html.parser", parse_only=SoupStrainer("a"))
    hyperlinks = []
    for line in soup.find_all('a', href=True):
        hyperlinks.append(line['href'].strip('/'))
    return hyperlinks

def find_latest_numeric_release(url, d):
    "Find the latest listed numeric release on the given url"
    max=0
    maxstr=""
    for link in get_links_from_url(url, d):
        try:
            # TODO use LooseVersion
            release = float(link)
        except:
            release = 0
        if release > max:
            max = release
            maxstr = link
    return maxstr

def is_src_rpm(name):
    "Check if the link is pointing to a src.rpm file"
    return name.endswith(".src.rpm")

def package_name_from_srpm(srpm):
    "Strip out the package name from the src.rpm filename"

    # ca-certificates-2016.2.7-1.0.fc24.src.rpm
    # ^name           ^ver     ^release^removed
    (name, version, release) = srpm.replace(".src.rpm", "").rsplit("-", 2)
    return name

def get_source_package_list_from_url(url, section, d):
    "Return a sectioned list of package names from a URL list"

    bb.note("Reading %s: %s" % (url, section))
    links = get_links_from_url(url, d)
    srpms = filter(is_src_rpm, links)
    names_list = map(package_name_from_srpm, srpms)

    new_pkgs = set()
    for pkgs in names_list:
       new_pkgs.add(pkgs + ":" + section)
    return new_pkgs

def get_source_package_list_from_url_by_letter(url, section, d):
    import string
    from urllib.error import HTTPError
    packages = set()
    for letter in (string.ascii_lowercase + string.digits):
        # Not all subfolders may exist, so silently handle 404
        try:
            packages |= get_source_package_list_from_url(url + "/" + letter, section, d)
        except HTTPError as e:
            if e.code != 404: raise
    return packages

def get_latest_released_fedora_source_package_list(d):
    "Returns list of all the name os packages in the latest fedora distro"
    latest = find_latest_numeric_release("http://archive.fedoraproject.org/pub/fedora/linux/releases/", d)
    package_names = get_source_package_list_from_url_by_letter("http://archive.fedoraproject.org/pub/fedora/linux/releases/%s/Everything/source/tree/Packages/" % latest, "main", d)
    package_names |= get_source_package_list_from_url_by_letter("http://archive.fedoraproject.org/pub/fedora/linux/updates/%s/SRPMS/" % latest, "updates", d)
    return latest, package_names

def get_latest_released_opensuse_source_package_list(d):
    "Returns list of all the name os packages in the latest opensuse distro"
    latest = find_latest_numeric_release("http://download.opensuse.org/source/distribution/leap", d)

    package_names = get_source_package_list_from_url("http://download.opensuse.org/source/distribution/leap/%s/repo/oss/suse/src/" % latest, "main", d)
    package_names |= get_source_package_list_from_url("http://download.opensuse.org/update/leap/%s/oss/src/" % latest, "updates", d)
    return latest, package_names

def get_latest_released_clear_source_package_list(d):
    latest = find_latest_numeric_release("https://download.clearlinux.org/releases/", d)
    package_names = get_source_package_list_from_url("https://download.clearlinux.org/releases/%s/clear/source/SRPMS/" % latest, "main", d)
    return latest, package_names

def find_latest_debian_release(url, d):
    "Find the latest listed debian release on the given url"

    releases = [link.replace("Debian", "")
                for link in get_links_from_url(url, d)
                if link.startswith("Debian")]
    releases.sort()
    try:
        return releases[-1]
    except:
        return "_NotFound_"

def get_debian_style_source_package_list(url, section, d):
    "Return the list of package-names stored in the debian style Sources.gz file"
    import gzip

    package_names = set()
    for line in gzip.open(create_socket(url, d), mode="rt"):
        if line.startswith("Package:"):
            pkg = line.split(":", 1)[1].strip()
            package_names.add(pkg + ":" + section)
    return package_names

def get_latest_released_debian_source_package_list(d):
    "Returns list of all the name of packages in the latest debian distro"
    latest = find_latest_debian_release("http://ftp.debian.org/debian/dists/", d)
    url = "http://ftp.debian.org/debian/dists/stable/main/source/Sources.gz"
    package_names = get_debian_style_source_package_list(url, "main", d)
    url = "http://ftp.debian.org/debian/dists/stable-proposed-updates/main/source/Sources.gz"
    package_names |= get_debian_style_source_package_list(url, "updates", d)
    return latest, package_names

def find_latest_ubuntu_release(url, d):
    """
    Find the latest listed Ubuntu release on the given ubuntu/dists/ URL.

    To avoid matching development releases look for distributions that have
    updates, so the resulting distro could be any supported release.
    """
    url += "?C=M;O=D" # Descending Sort by Last Modified
    for link in get_links_from_url(url, d):
        if "-updates" in link:
            distro = link.replace("-updates", "")
            return distro
    return "_NotFound_"

def get_latest_released_ubuntu_source_package_list(d):
    "Returns list of all the name os packages in the latest ubuntu distro"
    latest = find_latest_ubuntu_release("http://archive.ubuntu.com/ubuntu/dists/", d)
    url = "http://archive.ubuntu.com/ubuntu/dists/%s/main/source/Sources.gz" % latest
    package_names = get_debian_style_source_package_list(url, "main", d)
    url = "http://archive.ubuntu.com/ubuntu/dists/%s-updates/main/source/Sources.gz" % latest
    package_names |= get_debian_style_source_package_list(url, "updates", d)
    return latest, package_names

def create_distro_packages_list(distro_check_dir, d):
    import shutil

    pkglst_dir = os.path.join(distro_check_dir, "package_lists")
    bb.utils.remove(pkglst_dir, True)
    bb.utils.mkdirhier(pkglst_dir)

    per_distro_functions = (
                            ("Debian", get_latest_released_debian_source_package_list),
                            ("Ubuntu", get_latest_released_ubuntu_source_package_list),
                            ("Fedora", get_latest_released_fedora_source_package_list),
                            ("openSUSE", get_latest_released_opensuse_source_package_list),
                            ("Clear", get_latest_released_clear_source_package_list),
                           )

    for name, fetcher_func in per_distro_functions:
        try:
            release, package_list = fetcher_func(d)
        except Exception as e:
            bb.warn("Cannot fetch packages for %s: %s" % (name, e))
        bb.note("Distro: %s, Latest Release: %s, # src packages: %d" % (name, release, len(package_list)))
        if len(package_list) == 0:
            bb.error("Didn't fetch any packages for %s %s" % (name, release))

        package_list_file = os.path.join(pkglst_dir, name + "-" + release)
        with open(package_list_file, 'w') as f:
            for pkg in sorted(package_list):
                f.write(pkg + "\n")

def update_distro_data(distro_check_dir, datetime, d):
    """
    If distro packages list data is old then rebuild it.
    The operations has to be protected by a lock so that
    only one thread performes it at a time.
    """
    if not os.path.isdir (distro_check_dir):
        try:
            bb.note ("Making new directory: %s" % distro_check_dir)
            os.makedirs (distro_check_dir)
        except OSError:
            raise Exception('Unable to create directory %s' % (distro_check_dir))


    datetime_file = os.path.join(distro_check_dir, "build_datetime")
    saved_datetime = "_invalid_"
    import fcntl
    try:
        if not os.path.exists(datetime_file):
            open(datetime_file, 'w+').close() # touch the file so that the next open won't fail

        f = open(datetime_file, "r+")
        fcntl.lockf(f, fcntl.LOCK_EX)
        saved_datetime = f.read()
        if saved_datetime[0:8] != datetime[0:8]:
            bb.note("The build datetime did not match: saved:%s current:%s" % (saved_datetime, datetime))
            bb.note("Regenerating distro package lists")
            create_distro_packages_list(distro_check_dir, d)
            f.seek(0)
            f.write(datetime)

    except OSError as e:
        raise Exception('Unable to open timestamp: %s' % e)
    finally:
        fcntl.lockf(f, fcntl.LOCK_UN)
        f.close()

def compare_in_distro_packages_list(distro_check_dir, d):
    if not os.path.isdir(distro_check_dir):
        raise Exception("compare_in_distro_packages_list: invalid distro_check_dir passed")

    localdata = bb.data.createCopy(d)
    pkglst_dir = os.path.join(distro_check_dir, "package_lists")
    matching_distros = []
    pn = recipe_name = d.getVar('PN')
    bb.note("Checking: %s" % pn)

    if pn.find("-native") != -1:
        pnstripped = pn.split("-native")
        localdata.setVar('OVERRIDES', "pn-" + pnstripped[0] + ":" + d.getVar('OVERRIDES'))
        recipe_name = pnstripped[0]

    if pn.startswith("nativesdk-"):
        pnstripped = pn.split("nativesdk-")
        localdata.setVar('OVERRIDES', "pn-" + pnstripped[1] + ":" + d.getVar('OVERRIDES'))
        recipe_name = pnstripped[1]

    if pn.find("-cross") != -1:
        pnstripped = pn.split("-cross")
        localdata.setVar('OVERRIDES', "pn-" + pnstripped[0] + ":" + d.getVar('OVERRIDES'))
        recipe_name = pnstripped[0]

    if pn.find("-initial") != -1:
        pnstripped = pn.split("-initial")
        localdata.setVar('OVERRIDES', "pn-" + pnstripped[0] + ":" + d.getVar('OVERRIDES'))
        recipe_name = pnstripped[0]

    bb.note("Recipe: %s" % recipe_name)

    distro_exceptions = dict({"OE-Core":'OE-Core', "OpenedHand":'OpenedHand', "Intel":'Intel', "Upstream":'Upstream', "Windriver":'Windriver', "OSPDT":'OSPDT Approved', "Poky":'poky'})
    tmp = localdata.getVar('DISTRO_PN_ALIAS') or ""
    for str in tmp.split():
        if str and str.find("=") == -1 and distro_exceptions[str]:
            matching_distros.append(str)

    distro_pn_aliases = {}
    for str in tmp.split():
        if "=" in str:
            (dist, pn_alias) = str.split('=')
            distro_pn_aliases[dist.strip().lower()] = pn_alias.strip()

    for file in os.listdir(pkglst_dir):
        (distro, distro_release) = file.split("-")
        f = open(os.path.join(pkglst_dir, file), "r")
        for line in f:
            (pkg, section) = line.split(":")
            if distro.lower() in distro_pn_aliases:
                pn = distro_pn_aliases[distro.lower()]
            else:
                pn = recipe_name
            if pn == pkg:
                matching_distros.append(distro + "-" + section[:-1]) # strip the \n at the end
                f.close()
                break
        f.close()

    for item in tmp.split():
        matching_distros.append(item)
    bb.note("Matching: %s" % matching_distros)
    return matching_distros

def create_log_file(d, logname):
    logpath = d.getVar('LOG_DIR')
    bb.utils.mkdirhier(logpath)
    logfn, logsuffix = os.path.splitext(logname)
    logfile = os.path.join(logpath, "%s.%s%s" % (logfn, d.getVar('DATETIME'), logsuffix))
    if not os.path.exists(logfile):
            slogfile = os.path.join(logpath, logname)
            if os.path.exists(slogfile):
                    os.remove(slogfile)
            open(logfile, 'w+').close()
            os.symlink(logfile, slogfile)
            d.setVar('LOG_FILE', logfile)
    return logfile


def save_distro_check_result(result, datetime, result_file, d):
    pn = d.getVar('PN')
    logdir = d.getVar('LOG_DIR')
    if not logdir:
        bb.error("LOG_DIR variable is not defined, can't write the distro_check results")
        return
    bb.utils.mkdirhier(logdir)

    line = pn
    for i in result:
        line = line + "," + i
    f = open(result_file, "a")
    import fcntl
    fcntl.lockf(f, fcntl.LOCK_EX)
    f.seek(0, os.SEEK_END) # seek to the end of file
    f.write(line + "\n")
    fcntl.lockf(f, fcntl.LOCK_UN)
    f.close()
