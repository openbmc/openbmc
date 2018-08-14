# In order to support a deterministic set of 'dynamic' users/groups,
# we need a function to reformat the params based on a static file
def update_useradd_static_config(d):
    import itertools
    import re
    import errno
    import oe.useradd

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

    def handle_missing_id(id, type, pkg, files, var, value):
        # For backwards compatibility we accept "1" in addition to "error"
        error_dynamic = d.getVar('USERADD_ERROR_DYNAMIC')
        msg = "%s - %s: %sname %s does not have a static ID defined." % (d.getVar('PN'), pkg, type, id)
        if files:
            msg += " Add %s to one of these files: %s" % (id, files)
        else:
            msg += " %s file(s) not found in BBPATH: %s" % (var, value)
        if error_dynamic == 'error' or error_dynamic == '1':
            raise NotImplementedError(msg)
        elif error_dynamic == 'warn':
            bb.warn(msg)
        elif error_dynamic == 'skip':
            raise bb.parse.SkipRecipe(msg)

    # Return a list of configuration files based on either the default
    # files/group or the contents of USERADD_GID_TABLES, resp.
    # files/passwd for USERADD_UID_TABLES.
    # Paths are resolved via BBPATH.
    def get_table_list(d, var, default):
        files = []
        bbpath = d.getVar('BBPATH', True)
        tables = d.getVar(var, True)
        if not tables:
            tables = default
        for conf_file in tables.split():
            files.append(bb.utils.which(bbpath, conf_file))
        return (' '.join(files), var, default)

    # We parse and rewrite the useradd components
    def rewrite_useradd(params, is_pkg):
        parser = oe.useradd.build_useradd_parser()

        newparams = []
        users = None
        for param in oe.useradd.split_commands(params):
            try:
                uaargs = parser.parse_args(oe.useradd.split_args(param))
            except:
                bb.fatal("%s: Unable to parse arguments for USERADD_PARAM_%s: '%s'" % (d.getVar('PN'), pkg, param))

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
                files, table_var, table_value = get_table_list(d, 'USERADD_UID_TABLES', 'files/passwd')
                users = merge_files(files, 7)

            type = 'system user' if uaargs.system else 'normal user'
            if uaargs.LOGIN not in users:
                handle_missing_id(uaargs.LOGIN, type, pkg, files, table_var, table_value)
                newparams.append(param)
                continue

            field = users[uaargs.LOGIN]

            if uaargs.uid and field[2] and (uaargs.uid != field[2]):
                bb.warn("%s: Changing username %s's uid from (%s) to (%s), verify configuration files!" % (d.getVar('PN'), uaargs.LOGIN, uaargs.uid, field[2]))
            uaargs.uid = field[2] or uaargs.uid

            # Determine the possible groupname
            # Unless the group name (or gid) is specified, we assume that the LOGIN is the groupname
            #
            # By default the system has creation of the matching groups enabled
            # So if the implicit username-group creation is on, then the implicit groupname (LOGIN)
            # is used, and we disable the user_group option.
            #
            if uaargs.gid:
                uaargs.groupname = uaargs.gid
            elif uaargs.user_group is not False:
                uaargs.groupname = uaargs.LOGIN
            else:
                uaargs.groupname = 'users'
            uaargs.groupid = field[3] or uaargs.groupname

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
                    bb.warn("%s: Changing gid for login %s to %s, verify configuration files!" % (d.getVar('PN'), uaargs.LOGIN, uaargs.groupid))

                uaargs.gid = uaargs.groupid
                uaargs.user_group = None
                if newgroup and is_pkg:
                    groupadd = d.getVar("GROUPADD_PARAM_%s" % pkg)
                    if groupadd:
                        # Only add the group if not already specified
                        if not uaargs.groupname in groupadd:
                            d.setVar("GROUPADD_PARAM_%s" % pkg, "%s; %s" % (groupadd, newgroup))
                    else:
                        d.setVar("GROUPADD_PARAM_%s" % pkg, newgroup)

            uaargs.comment = "'%s'" % field[4] if field[4] else uaargs.comment
            uaargs.home_dir = field[5] or uaargs.home_dir
            uaargs.shell = field[6] or uaargs.shell

            # Should be an error if a specific option is set...
            if not uaargs.uid or not uaargs.uid.isdigit() or not uaargs.gid:
                 handle_missing_id(uaargs.LOGIN, type, pkg, files, table_var, table_value)

            # Reconstruct the args...
            newparam  = ['', ' --defaults'][uaargs.defaults]
            newparam += ['', ' --base-dir %s' % uaargs.base_dir][uaargs.base_dir != None]
            newparam += ['', ' --comment %s' % uaargs.comment][uaargs.comment != None]
            newparam += ['', ' --home-dir %s' % uaargs.home_dir][uaargs.home_dir != None]
            newparam += ['', ' --expiredate %s' % uaargs.expiredate][uaargs.expiredate != None]
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
            if uaargs.password != None:
                newparam += ['', ' --password %s' % uaargs.password][uaargs.password != None]
            elif uaargs.clear_password:
                newparam += ['', ' --clear-password %s' % uaargs.clear_password][uaargs.clear_password != None]
            newparam += ['', ' --root %s' % uaargs.root][uaargs.root != None]
            newparam += ['', ' --system'][uaargs.system]
            newparam += ['', ' --shell %s' % uaargs.shell][uaargs.shell != None]
            newparam += ['', ' --uid %s' % uaargs.uid][uaargs.uid != None]
            newparam += ['', ' --user-group'][uaargs.user_group is True]
            newparam += ' %s' % uaargs.LOGIN

            newparams.append(newparam)

        return ";".join(newparams).strip()

    # We parse and rewrite the groupadd components
    def rewrite_groupadd(params, is_pkg):
        parser = oe.useradd.build_groupadd_parser()

        newparams = []
        groups = None
        for param in oe.useradd.split_commands(params):
            try:
                # If we're processing multiple lines, we could have left over values here...
                gaargs = parser.parse_args(oe.useradd.split_args(param))
            except:
                bb.fatal("%s: Unable to parse arguments for GROUPADD_PARAM_%s: '%s'" % (d.getVar('PN'), pkg, param))

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
                files, table_var, table_value = get_table_list(d, 'USERADD_GID_TABLES', 'files/group')
                groups = merge_files(files, 4)

            type = 'system group' if gaargs.system else 'normal group'
            if gaargs.GROUP not in groups:
                handle_missing_id(gaargs.GROUP, type, pkg, files, table_var, table_value)
                newparams.append(param)
                continue

            field = groups[gaargs.GROUP]

            if field[2]:
                if gaargs.gid and (gaargs.gid != field[2]):
                    bb.warn("%s: Changing groupname %s's gid from (%s) to (%s), verify configuration files!" % (d.getVar('PN'), gaargs.GROUP, gaargs.gid, field[2]))
                gaargs.gid = field[2]

            if not gaargs.gid or not gaargs.gid.isdigit():
                handle_missing_id(gaargs.GROUP, type, pkg, files, table_var, table_value)

            # Reconstruct the args...
            newparam  = ['', ' --force'][gaargs.force]
            newparam += ['', ' --gid %s' % gaargs.gid][gaargs.gid != None]
            newparam += ['', ' --key %s' % gaargs.key][gaargs.key != None]
            newparam += ['', ' --non-unique'][gaargs.non_unique]
            if gaargs.password != None:
                newparam += ['', ' --password %s' % gaargs.password][gaargs.password != None]
            elif gaargs.clear_password:
                newparam += ['', ' --clear-password %s' % gaargs.clear_password][gaargs.clear_password != None]
            newparam += ['', ' --root %s' % gaargs.root][gaargs.root != None]
            newparam += ['', ' --system'][gaargs.system]
            newparam += ' %s' % gaargs.GROUP

            newparams.append(newparam)

        return ";".join(newparams).strip()

    # The parsing of the current recipe depends on the content of
    # the files listed in USERADD_UID/GID_TABLES. We need to tell bitbake
    # about that explicitly to trigger re-parsing and thus re-execution of
    # this code when the files change.
    bbpath = d.getVar('BBPATH')
    for varname, default in (('USERADD_UID_TABLES', 'files/passwd'),
                             ('USERADD_GID_TABLES', 'files/group')):
        tables = d.getVar(varname)
        if not tables:
            tables = default
        for conf_file in tables.split():
            bb.parse.mark_dependency(d, bb.utils.which(bbpath, conf_file))

    # Load and process the users and groups, rewriting the adduser/addgroup params
    useradd_packages = d.getVar('USERADD_PACKAGES') or ""

    for pkg in useradd_packages.split():
        # Groupmems doesn't have anything we might want to change, so simply validating
        # is a bit of a waste -- only process useradd/groupadd
        useradd_param = d.getVar('USERADD_PARAM_%s' % pkg)
        if useradd_param:
            #bb.warn("Before: 'USERADD_PARAM_%s' - '%s'" % (pkg, useradd_param))
            d.setVar('USERADD_PARAM_%s' % pkg, rewrite_useradd(useradd_param, True))
            #bb.warn("After:  'USERADD_PARAM_%s' - '%s'" % (pkg, d.getVar('USERADD_PARAM_%s' % pkg)))

        groupadd_param = d.getVar('GROUPADD_PARAM_%s' % pkg)
        if groupadd_param:
            #bb.warn("Before: 'GROUPADD_PARAM_%s' - '%s'" % (pkg, groupadd_param))
            d.setVar('GROUPADD_PARAM_%s' % pkg, rewrite_groupadd(groupadd_param, True))
            #bb.warn("After:  'GROUPADD_PARAM_%s' - '%s'" % (pkg, d.getVar('GROUPADD_PARAM_%s' % pkg)))

    # Load and process extra users and groups, rewriting only adduser/addgroup params
    pkg = d.getVar('PN')
    extrausers = d.getVar('EXTRA_USERS_PARAMS') or ""

    #bb.warn("Before:  'EXTRA_USERS_PARAMS' - '%s'" % (d.getVar('EXTRA_USERS_PARAMS')))
    new_extrausers = []
    for cmd in oe.useradd.split_commands(extrausers):
        if re.match('''useradd (.*)''', cmd):
            useradd_param = re.match('''useradd (.*)''', cmd).group(1)
            useradd_param = rewrite_useradd(useradd_param, False)
            cmd = 'useradd %s' % useradd_param
        elif re.match('''groupadd (.*)''', cmd):
            groupadd_param = re.match('''groupadd (.*)''', cmd).group(1)
            groupadd_param = rewrite_groupadd(groupadd_param, False)
            cmd = 'groupadd %s' % groupadd_param

        new_extrausers.append(cmd)

    new_extrausers.append('')
    d.setVar('EXTRA_USERS_PARAMS', ';'.join(new_extrausers))
    #bb.warn("After:  'EXTRA_USERS_PARAMS' - '%s'" % (d.getVar('EXTRA_USERS_PARAMS')))


python __anonymous() {
    if not bb.data.inherits_class('nativesdk', d) \
        and not bb.data.inherits_class('native', d):
        try:
            update_useradd_static_config(d)
        except NotImplementedError as f:
            bb.debug(1, "Skipping recipe %s: %s" % (d.getVar('PN'), f))
            raise bb.parse.SkipRecipe(f)
}
