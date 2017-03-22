# In order to support a deterministic set of 'dynamic' users/groups,
# we need a function to reformat the params based on a static file
def update_useradd_static_config(d):
    import argparse
    import itertools
    import re
    import errno

    class myArgumentParser( argparse.ArgumentParser ):
        def _print_message(self, message, file=None):
            bb.warn("%s - %s: %s" % (d.getVar('PN', True), pkg, message))

        # This should never be called...
        def exit(self, status=0, message=None):
            message = message or ("%s - %s: useradd.bbclass: Argument parsing exited" % (d.getVar('PN', True), pkg))
            error(message)

        def error(self, message):
            bb.fatal(message)

    def list_extend(iterable, length, obj = None):
        """Ensure that iterable is the specified length by extending with obj
        and return it as a list"""
        return list(itertools.islice(itertools.chain(iterable, itertools.repeat(obj)), length))

    def merge_files(file_list, exp_fields):
        """Read each passwd/group file in file_list, split each line and create
        a dictionary with the user/group names as keys and the split lines as
        values. If the user/group name already exists in the dictionary, then
        update any fields in the list with the values from the new list (if they
        are set)."""
        id_table = dict()
        for conf in file_list.split():
            try:
                with open(conf, "r") as f:
                    for line in f:
                        if line.startswith('#'):
                            continue
                        # Make sure there always are at least exp_fields
                        # elements in the field list. This allows for leaving
                        # out trailing colons in the files.
                        fields = list_extend(line.rstrip().split(":"), exp_fields)
                        if fields[0] not in id_table:
                            id_table[fields[0]] = fields
                        else:
                            id_table[fields[0]] = list(map(lambda x, y: x or y, fields, id_table[fields[0]]))
            except IOError as e:
                if e.errno == errno.ENOENT:
                    pass

        return id_table

    def handle_missing_id(id, type, pkg):
        # For backwards compatibility we accept "1" in addition to "error"
        if d.getVar('USERADD_ERROR_DYNAMIC', True) == 'error' or d.getVar('USERADD_ERROR_DYNAMIC', True) == '1':
            #bb.error("Skipping recipe %s, package %s which adds %sname %s does not have a static ID defined." % (d.getVar('PN', True),  pkg, type, id))
            bb.fatal("%s - %s: %sname %s does not have a static ID defined." % (d.getVar('PN', True), pkg, type, id))
        elif d.getVar('USERADD_ERROR_DYNAMIC', True) == 'warn':
            bb.warn("%s - %s: %sname %s does not have a static ID defined." % (d.getVar('PN', True), pkg, type, id))

    # We parse and rewrite the useradd components
    def rewrite_useradd(params):
        # The following comes from --help on useradd from shadow
        parser = myArgumentParser(prog='useradd')
        parser.add_argument("-b", "--base-dir", metavar="BASE_DIR", help="base directory for the home directory of the new account")
        parser.add_argument("-c", "--comment", metavar="COMMENT", help="GECOS field of the new account")
        parser.add_argument("-d", "--home-dir", metavar="HOME_DIR", help="home directory of the new account")
        parser.add_argument("-D", "--defaults", help="print or change default useradd configuration", action="store_true")
        parser.add_argument("-e", "--expiredate", metavar="EXPIRE_DATE", help="expiration date of the new account")
        parser.add_argument("-f", "--inactive", metavar="INACTIVE", help="password inactivity period of the new account")
        parser.add_argument("-g", "--gid", metavar="GROUP", help="name or ID of the primary group of the new account")
        parser.add_argument("-G", "--groups", metavar="GROUPS", help="list of supplementary groups of the new account")
        parser.add_argument("-k", "--skel", metavar="SKEL_DIR", help="use this alternative skeleton directory")
        parser.add_argument("-K", "--key", metavar="KEY=VALUE", help="override /etc/login.defs defaults")
        parser.add_argument("-l", "--no-log-init", help="do not add the user to the lastlog and faillog databases", action="store_true")
        parser.add_argument("-m", "--create-home", help="create the user's home directory", action="store_const", const=True)
        parser.add_argument("-M", "--no-create-home", dest="create_home", help="do not create the user's home directory", action="store_const", const=False)
        parser.add_argument("-N", "--no-user-group", dest="user_group", help="do not create a group with the same name as the user", action="store_const", const=False)
        parser.add_argument("-o", "--non-unique", help="allow to create users with duplicate (non-unique UID)", action="store_true")
        parser.add_argument("-p", "--password", metavar="PASSWORD", help="encrypted password of the new account")
        parser.add_argument("-R", "--root", metavar="CHROOT_DIR", help="directory to chroot into")
        parser.add_argument("-r", "--system", help="create a system account", action="store_true")
        parser.add_argument("-s", "--shell", metavar="SHELL", help="login shell of the new account")
        parser.add_argument("-u", "--uid", metavar="UID", help="user ID of the new account")
        parser.add_argument("-U", "--user-group", help="create a group with the same name as the user", action="store_const", const=True)
        parser.add_argument("LOGIN", help="Login name of the new user")

        # Return a list of configuration files based on either the default
        # files/passwd or the contents of USERADD_UID_TABLES
        # paths are resolved via BBPATH
        def get_passwd_list(d):
            str = ""
            bbpath = d.getVar('BBPATH', True)
            passwd_tables = d.getVar('USERADD_UID_TABLES', True)
            if not passwd_tables:
                passwd_tables = 'files/passwd'
            for conf_file in passwd_tables.split():
                str += " %s" % bb.utils.which(bbpath, conf_file)
            return str

        newparams = []
        users = None
        for param in re.split('''[ \t]*;[ \t]*(?=(?:[^'"]|'[^']*'|"[^"]*")*$)''', params):
            param = param.strip()
            if not param:
                continue
            try:
                uaargs = parser.parse_args(re.split('''[ \t]+(?=(?:[^'"]|'[^']*'|"[^"]*")*$)''', param))
            except:
                bb.fatal("%s: Unable to parse arguments for USERADD_PARAM_%s: '%s'" % (d.getVar('PN', True), pkg, param))

            # Read all passwd files specified in USERADD_UID_TABLES or files/passwd
            # Use the standard passwd layout:
            #  username:password:user_id:group_id:comment:home_directory:login_shell
            #
            # If a field is left blank, the original value will be used.  The 'username'
            # field is required.
            #
            # Note: we ignore the password field, as including even the hashed password
            # in the useradd command may introduce a security hole.  It's assumed that
            # all new users get the default ('*' which prevents login) until the user is
            # specifically configured by the system admin.
            if not users:
                users = merge_files(get_passwd_list(d), 7)

            if uaargs.LOGIN not in users:
                if not uaargs.uid or not uaargs.uid.isdigit() or not uaargs.gid:
                    handle_missing_id(uaargs.LOGIN, 'user', pkg)
                continue

            field = users[uaargs.LOGIN]

            if uaargs.uid and field[2] and (uaargs.uid != field[2]):
                bb.warn("%s: Changing username %s's uid from (%s) to (%s), verify configuration files!" % (d.getVar('PN', True), uaargs.LOGIN, uaargs.uid, field[2]))
            uaargs.uid = field[2] or uaargs.uid

            # Determine the possible groupname
            # Unless the group name (or gid) is specified, we assume that the LOGIN is the groupname
            #
            # By default the system has creation of the matching groups enabled
            # So if the implicit username-group creation is on, then the implicit groupname (LOGIN)
            # is used, and we disable the user_group option.
            #
            user_group = uaargs.user_group is None or uaargs.user_group is True
            uaargs.groupname = uaargs.LOGIN if user_group else uaargs.gid
            uaargs.groupid = field[3] or uaargs.gid or uaargs.groupname

            if uaargs.groupid and uaargs.gid != uaargs.groupid:
                newgroup = None
                if not uaargs.groupid.isdigit():
                    # We don't have a group number, so we have to add a name
                    bb.debug(1, "Adding group %s!" % uaargs.groupid)
                    newgroup = "%s %s" % (' --system' if uaargs.system else '', uaargs.groupid)
                elif uaargs.groupname and not uaargs.groupname.isdigit():
                    # We have a group name and a group number to assign it to
                    bb.debug(1, "Adding group %s (gid %s)!" % (uaargs.groupname, uaargs.groupid))
                    newgroup = "-g %s %s" % (uaargs.groupid, uaargs.groupname)
                else:
                    # We want to add a group, but we don't know it's name... so we can't add the group...
                    # We have to assume the group has previously been added or we'll fail on the adduser...
                    # Note: specifying the actual gid is very rare in OE, usually the group name is specified.
                    bb.warn("%s: Changing gid for login %s to %s, verify configuration files!" % (d.getVar('PN', True), uaargs.LOGIN, uaargs.groupid))

                uaargs.gid = uaargs.groupid
                uaargs.user_group = None
                if newgroup:
                    groupadd = d.getVar("GROUPADD_PARAM_%s" % pkg, True)
                    if groupadd:
                        d.setVar("GROUPADD_PARAM_%s" % pkg, "%s; %s" % (groupadd, newgroup))
                    else:
                        d.setVar("GROUPADD_PARAM_%s" % pkg, newgroup)

            uaargs.comment = "'%s'" % field[4] if field[4] else uaargs.comment
            uaargs.home_dir = field[5] or uaargs.home_dir
            uaargs.shell = field[6] or uaargs.shell

            # Should be an error if a specific option is set...
            if not uaargs.uid or not uaargs.uid.isdigit() or not uaargs.gid:
                 handle_missing_id(uaargs.LOGIN, 'user', pkg)

            # Reconstruct the args...
            newparam  = ['', ' --defaults'][uaargs.defaults]
            newparam += ['', ' --base-dir %s' % uaargs.base_dir][uaargs.base_dir != None]
            newparam += ['', ' --comment %s' % uaargs.comment][uaargs.comment != None]
            newparam += ['', ' --home-dir %s' % uaargs.home_dir][uaargs.home_dir != None]
            newparam += ['', ' --expiredata %s' % uaargs.expiredate][uaargs.expiredate != None]
            newparam += ['', ' --inactive %s' % uaargs.inactive][uaargs.inactive != None]
            newparam += ['', ' --gid %s' % uaargs.gid][uaargs.gid != None]
            newparam += ['', ' --groups %s' % uaargs.groups][uaargs.groups != None]
            newparam += ['', ' --skel %s' % uaargs.skel][uaargs.skel != None]
            newparam += ['', ' --key %s' % uaargs.key][uaargs.key != None]
            newparam += ['', ' --no-log-init'][uaargs.no_log_init]
            newparam += ['', ' --create-home'][uaargs.create_home is True]
            newparam += ['', ' --no-create-home'][uaargs.create_home is False]
            newparam += ['', ' --no-user-group'][uaargs.user_group is False]
            newparam += ['', ' --non-unique'][uaargs.non_unique]
            newparam += ['', ' --password %s' % uaargs.password][uaargs.password != None]
            newparam += ['', ' --root %s' % uaargs.root][uaargs.root != None]
            newparam += ['', ' --system'][uaargs.system]
            newparam += ['', ' --shell %s' % uaargs.shell][uaargs.shell != None]
            newparam += ['', ' --uid %s' % uaargs.uid][uaargs.uid != None]
            newparam += ['', ' --user-group'][uaargs.user_group is True]
            newparam += ' %s' % uaargs.LOGIN

            newparams.append(newparam)

        return ";".join(newparams).strip()

    # We parse and rewrite the groupadd components
    def rewrite_groupadd(params):
        # The following comes from --help on groupadd from shadow
        parser = myArgumentParser(prog='groupadd')
        parser.add_argument("-f", "--force", help="exit successfully if the group already exists, and cancel -g if the GID is already used", action="store_true")
        parser.add_argument("-g", "--gid", metavar="GID", help="use GID for the new group")
        parser.add_argument("-K", "--key", metavar="KEY=VALUE", help="override /etc/login.defs defaults")
        parser.add_argument("-o", "--non-unique", help="allow to create groups with duplicate (non-unique) GID", action="store_true")
        parser.add_argument("-p", "--password", metavar="PASSWORD", help="use this encrypted password for the new group")
        parser.add_argument("-R", "--root", metavar="CHROOT_DIR", help="directory to chroot into")
        parser.add_argument("-r", "--system", help="create a system account", action="store_true")
        parser.add_argument("GROUP", help="Group name of the new group")

        # Return a list of configuration files based on either the default
        # files/group or the contents of USERADD_GID_TABLES
        # paths are resolved via BBPATH
        def get_group_list(d):
            str = ""
            bbpath = d.getVar('BBPATH', True)
            group_tables = d.getVar('USERADD_GID_TABLES', True)
            if not group_tables:
                group_tables = 'files/group'
            for conf_file in group_tables.split():
                str += " %s" % bb.utils.which(bbpath, conf_file)
            return str

        newparams = []
        groups = None
        for param in re.split('''[ \t]*;[ \t]*(?=(?:[^'"]|'[^']*'|"[^"]*")*$)''', params):
            param = param.strip()
            if not param:
                continue
            try:
                # If we're processing multiple lines, we could have left over values here...
                gaargs = parser.parse_args(re.split('''[ \t]+(?=(?:[^'"]|'[^']*'|"[^"]*")*$)''', param))
            except:
                bb.fatal("%s: Unable to parse arguments for GROUPADD_PARAM_%s: '%s'" % (d.getVar('PN', True), pkg, param))

            # Read all group files specified in USERADD_GID_TABLES or files/group
            # Use the standard group layout:
            #  groupname:password:group_id:group_members
            #
            # If a field is left blank, the original value will be used. The 'groupname' field
            # is required.
            #
            # Note: similar to the passwd file, the 'password' filed is ignored
            # Note: group_members is ignored, group members must be configured with the GROUPMEMS_PARAM
            if not groups:
                groups = merge_files(get_group_list(d), 4)

            if gaargs.GROUP not in groups:
                if not gaargs.gid or not gaargs.gid.isdigit():
                    handle_missing_id(gaargs.GROUP, 'group', pkg)
                continue

            field = groups[gaargs.GROUP]

            if field[2]:
                if gaargs.gid and (gaargs.gid != field[2]):
                    bb.warn("%s: Changing groupname %s's gid from (%s) to (%s), verify configuration files!" % (d.getVar('PN', True), gaargs.GROUP, gaargs.gid, field[2]))
                gaargs.gid = field[2]

            if not gaargs.gid or not gaargs.gid.isdigit():
                handle_missing_id(gaargs.GROUP, 'group', pkg)

            # Reconstruct the args...
            newparam  = ['', ' --force'][gaargs.force]
            newparam += ['', ' --gid %s' % gaargs.gid][gaargs.gid != None]
            newparam += ['', ' --key %s' % gaargs.key][gaargs.key != None]
            newparam += ['', ' --non-unique'][gaargs.non_unique]
            newparam += ['', ' --password %s' % gaargs.password][gaargs.password != None]
            newparam += ['', ' --root %s' % gaargs.root][gaargs.root != None]
            newparam += ['', ' --system'][gaargs.system]
            newparam += ' %s' % gaargs.GROUP

            newparams.append(newparam)

        return ";".join(newparams).strip()

    # The parsing of the current recipe depends on the content of
    # the files listed in USERADD_UID/GID_TABLES. We need to tell bitbake
    # about that explicitly to trigger re-parsing and thus re-execution of
    # this code when the files change.
    bbpath = d.getVar('BBPATH', True)
    for varname, default in (('USERADD_UID_TABLES', 'files/passwd'),
                             ('USERADD_GID_TABLES', 'files/group')):
        tables = d.getVar(varname, True)
        if not tables:
            tables = default
        for conf_file in tables.split():
            bb.parse.mark_dependency(d, bb.utils.which(bbpath, conf_file))

    # Load and process the users and groups, rewriting the adduser/addgroup params
    useradd_packages = d.getVar('USERADD_PACKAGES', True)

    for pkg in useradd_packages.split():
        # Groupmems doesn't have anything we might want to change, so simply validating
        # is a bit of a waste -- only process useradd/groupadd
        useradd_param = d.getVar('USERADD_PARAM_%s' % pkg, True)
        if useradd_param:
            #bb.warn("Before: 'USERADD_PARAM_%s' - '%s'" % (pkg, useradd_param))
            d.setVar('USERADD_PARAM_%s' % pkg, rewrite_useradd(useradd_param))
            #bb.warn("After:  'USERADD_PARAM_%s' - '%s'" % (pkg, d.getVar('USERADD_PARAM_%s' % pkg, True)))

        groupadd_param = d.getVar('GROUPADD_PARAM_%s' % pkg, True)
        if groupadd_param:
            #bb.warn("Before: 'GROUPADD_PARAM_%s' - '%s'" % (pkg, groupadd_param))
            d.setVar('GROUPADD_PARAM_%s' % pkg, rewrite_groupadd(groupadd_param))
            #bb.warn("After:  'GROUPADD_PARAM_%s' - '%s'" % (pkg, d.getVar('GROUPADD_PARAM_%s' % pkg, True)))



python __anonymous() {
    if not bb.data.inherits_class('nativesdk', d) \
        and not bb.data.inherits_class('native', d):
        try:
            update_useradd_static_config(d)
        except bb.build.FuncFailed as f:
            bb.debug(1, "Skipping recipe %s: %s" % (d.getVar('PN', True), f))
            raise bb.parse.SkipPackage(f)
}
