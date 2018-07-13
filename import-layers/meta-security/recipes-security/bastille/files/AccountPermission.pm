package Bastille::API::AccountPermission;
use strict;

use Bastille::API;

use Bastille::API::HPSpecific;

require Exporter;
our @ISA = qw(Exporter);
our @EXPORT_OK = qw(
B_chmod
B_chmod_if_exists
B_chown
B_chown_link
B_chgrp
B_chgrp_link
B_userdel
B_groupdel
B_remove_user_from_group
B_check_owner_group
B_is_unowned_file
B_is_ungrouped_file
B_check_permissions
B_permission_test
B_find_homes
B_is_executable
B_is_suid
B_is_sgid
B_get_user_list
B_get_group_list
B_remove_suid
);
our @EXPORT = @EXPORT_OK;

###########################################################################
# &B_chmod ($mode, $file) sets the mode of $file to $mode.  $mode must
# be stored in octal, so if you want to give mode 700 to /etc/aliases,
# you need to use:
#
#                 &B_chmod ( 0700 , "/etc/aliases");
#
# where the 0700 denotes "octal 7-0-0".
#
# &B_chmod ($mode_changes,$file) also respects the symbolic methods of
# changing file permissions, which are often what question authors are
# really seeking.
#
#                 &B_chmod ("u-s" , "/bin/mount")
# or
#                 &B_chmod ("go-rwx", "/bin/mount")
#
#
# &B_chmod respects GLOBAL_LOGONLY and uses
# &B_revert_log used to insert a shell command that will return
#         the permissions to the pre-Bastille state.
#
# B_chmod allow for globbing now, as of 1.2.0.  JJB
#
##########################################################################


sub B_chmod($$) {
   my ($new_perm,$file_expr)=@_;
   my $old_perm;
   my $old_perm_raw;
   my $new_perm_formatted;
   my $old_perm_formatted;

   my $retval=1;

   my $symbolic = 0;
   my ($chmod_noun,$add_remove,$capability) = ();
   # Handle symbolic possibilities too
   if ($new_perm =~ /([ugo]+)([+-]{1})([rwxst]+)/) {
       $symbolic = 1;
       $chmod_noun = $1;
       $add_remove = $2;
       $capability = $3;
   }

   my $file;
   my @files = glob ($file_expr);

   foreach $file (@files) {

       # Prepend global prefix, but save the original filename for B_backup_file
       my $original_file=$file;

       # Store the old permissions so that we can log them.
       unless (stat $file) {
           &B_log("ERROR","Couldn't stat $original_file from $old_perm to change permissions\n");
           next;
       }

       $old_perm_raw=(stat(_))[2];
       $old_perm= (($old_perm_raw/512) % 8) .
           (($old_perm_raw/64) % 8) .
               (($old_perm_raw/8) % 8) .
                   ($old_perm_raw % 8);

       # If we've gone symbolic, calculate the new permissions in octal.
       if ($symbolic) {
           #
           # We calculate the new permissions by applying a bitmask to
           # the current permissions, by OR-ing (for +) or XOR-ing (for -).
           #
           # We create this mask by first calculating a perm_mask that forms
           # the right side of this, then multiplying it by 8 raised to the
           # appropriate power to affect the correct digit of the octal mask.
           # This means that we raise 8 to the power of 0,1,2, or 3, based on
           # the noun of "other","group","user", or "suid/sgid/sticky".
           #
           # Actually, we handle multiple nouns by summing powers of 8.
           #
           # The only tough part is that we have to handle suid/sgid/sticky
           # differently.
           #

           # We're going to calculate a mask to OR or XOR with the current
           # file mode.  This mask is $mask.  We calculate this by calculating
           # a sum of powers of 8, corresponding to user/group/other,
           # multiplied with a $premask.  The $premask is simply the
           # corresponding bitwise expression of the rwx bits.
           #
           # To handle SUID, SGID or sticky in the simplest way possible, we
           # simply add their values to the $mask first.

           my $perm_mask = 00;
           my $mask = 00;

           # Check for SUID, SGID or sticky as these are exceptional.
           if ($capability =~ /s/) {
               if ($chmod_noun =~ /u/) {
                   $mask += 04000;
               }
               if ($chmod_noun =~ /g/) {
                   $mask += 02000;
               }
           }
           if ($capability =~ /t/) {
               $mask += 01000;
           }

           # Now handle the normal attributes
           if ($capability =~ /[rwx]/) {
               if ($capability =~ /r/) {
                   $perm_mask |= 04;
               }
               if ($capability =~ /w/) {
                   $perm_mask |= 02;
               }
               if ($capability =~ /x/) {
                   $perm_mask |= 01;
               }

               # Now figure out which 3 bit octal digit we're affecting.
               my $power = 0;
               if ($chmod_noun =~ /u/) {
                   $mask += $perm_mask * 64;
               }
               if ($chmod_noun =~ /g/) {
                   $mask += $perm_mask * 8;
               }
               if ($chmod_noun =~ /o/) {
                   $mask += $perm_mask * 1;
               }
           }
           # Now apply the mask to get the new permissions
           if ($add_remove eq '+') {
               $new_perm = $old_perm_raw | $mask;
           }
           elsif ($add_remove eq '-') {
               $new_perm = $old_perm_raw & ( ~($mask) );
           }
       }

       # formating for simple long octal output of the permissions in string form
       $new_perm_formatted=sprintf "%5lo",$new_perm;
       $old_perm_formatted=sprintf "%5lo",$old_perm_raw;

       &B_log("ACTION","change permissions on $original_file from $old_perm_formatted to $new_perm_formatted\n");

       &B_log("ACTION", "chmod $new_perm_formatted,\"$original_file\";\n");

       # Change the permissions on the file

       if ( -e $file ) {
           unless ($GLOBAL_LOGONLY) {
               $retval=chmod $new_perm,$file;
               if($retval){
                   # if the distribution is HP-UX then the modifications should
                   # also be made to the IPD (installed product database)
                   if(&GetDistro =~ "^HP-UX"){
                       &B_swmodify($file);
                   }
                   # making changes revert-able
                   &B_revert_log(&getGlobal('BIN', "chmod") . " $old_perm $file\n");
               }
           }
           unless ($retval) {
               &B_log("ERROR","Couldn't change permissions on $original_file from $old_perm_formatted to $new_perm_formatted\n");
               $retval=0;
           }
       }
       else {
           &B_log("ERROR", "chmod: File $original_file doesn't exist!\n");
           $retval=0;
       }
   }

   $retval;

}

###########################################################################
# &B_chmod_if_exists ($mode, $file) sets the mode of $file to $mode *if*
# $file exists.  $mode must be stored in octal, so if you want to give
# mode 700 to /etc/aliases, you need to use:
#
#                 &B_chmod_if_exists ( 0700 , "/etc/aliases");
#
# where the 0700 denotes "octal 7-0-0".
#
# &B_chmod_if_exists respects GLOBAL_LOGONLY and uses
# &B_revert_log to reset the permissions of the file.
#
# B_chmod_if_exists allow for globbing now, as of 1.2.0.  JJB
#
##########################################################################


sub B_chmod_if_exists($$) {
   my ($new_perm,$file_expr)=@_;
   # If $file_expr has a glob character, pass it on (B_chmod won't complain
   # about nonexistent files if given a glob pattern)
   if ( $file_expr =~ /[\*\[\{]/ ) {   # } just to match open brace for vi
       &B_log("ACTION","Running chmod $new_perm $file_expr");
       return(&B_chmod($new_perm,$file_expr));
   }
   # otherwise, test for file existence
   if ( -e $file_expr ) {
       &B_log("ACTION","File exists, running chmod $new_perm $file_expr");
       return(&B_chmod($new_perm,$file_expr));
   }
}

###########################################################################
# &B_chown ($uid, $file) sets the owner of $file to $uid, like this:
#
#                 &B_chown ( 0 , "/etc/aliases");
#
# &B_chown respects $GLOBAL_LOGONLY  and uses
# &B_revert_log to insert a shell command that will return
#         the file/directory owner to the pre-Bastille state.
#
# Unlike Perl, we've broken the chown function into B_chown/B_chgrp to
# make error checking simpler.
#
# As of 1.2.0, this now supports file globbing. JJB
#
##########################################################################


sub B_chown($$) {
   my ($newown,$file_expr)=@_;
   my $oldown;
   my $oldgown;

   my $retval=1;

   my $file;
   my @files = glob($file_expr);

   foreach $file (@files) {

       # Prepend prefix, but save original filename
       my $original_file=$file;

       $oldown=(stat $file)[4];
       $oldgown=(stat $file)[5];

       &B_log("ACTION","change ownership on $original_file from $oldown to $newown\n");
       &B_log("ACTION","chown $newown,$oldgown,\"$original_file\";\n");
       if ( -e $file ) {
           unless ($GLOBAL_LOGONLY) {
               # changing the files owner using perl chown function
               $retval = chown $newown,$oldgown,$file;
               if($retval){
                   # if the distribution is HP-UX then the modifications should
                   # also be made to the IPD (installed product database)
                   if(&GetDistro =~ "^HP-UX"){
                       &B_swmodify($file);
                   }
                   # making ownership change revert-able
                   &B_revert_log(&getGlobal('BIN', "chown") . " $oldown $file\n");
               }
           }
           unless ($retval) {
               &B_log("ERROR","Couldn't change ownership to $newown on file $original_file\n");
           }
       }
       else {
           &B_log("ERROR","chown: File $original_file doesn't exist!\n");
           $retval=0;
       }
   }

   $retval;
}

###########################################################################
# &B_chown_link just like &B_chown but one exception:
# if the input file is a link  it will not change the target's ownship, it only change the link itself's ownship
###########################################################################
sub B_chown_link($$){
    my ($newown,$file_expr)=@_;
    my $chown = &getGlobal("BIN","chown");
    my @files = glob($file_expr);
    my $retval = 1;

    foreach my $file (@files) {
        # Prepend prefix, but save original filename
        my $original_file=$file;
        my $oldown=(stat $file)[4];
        my $oldgown=(stat $file)[5];

        &B_log("ACTION","change ownership on $original_file from $oldown to $newown\n");
        &B_log("ACTION","chown -h $newown,\"$original_file\";\n");
        if ( -e $file ) {
            unless ($GLOBAL_LOGONLY) {
                `$chown -h $newown $file`;
                $retval = ($? >> 8);
                if($retval == 0 ){
                    # if the distribution is HP-UX then the modifications should
                    # also be made to the IPD (installed product database)
                    if(&GetDistro =~ "^HP-UX"){
                        &B_swmodify($file);
                    }
                    # making ownership change revert-able
                    &B_revert_log("$chown -h $oldown $file\n");
                }
            }
            unless ( ! $retval) {
                &B_log("ERROR","Couldn't change ownership to $newown on file $original_file\n");
            }
        }
        else {
            &B_log("ERROR","chown: File $original_file doesn't exist!\n");
            $retval=0;
        }
    }
}


###########################################################################
# &B_chgrp ($gid, $file) sets the group owner of $file to $gid, like this:
#
#                 &B_chgrp ( 0 , "/etc/aliases");
#
# &B_chgrp respects $GLOBAL_LOGONLY  and uses
# &B_revert_log to insert a shell command that will return
#         the file/directory group to the pre-Bastille state.
#
# Unlike Perl, we've broken the chown function into B_chown/B_chgrp to
# make error checking simpler.
#
# As of 1.2.0, this now supports file globbing.  JJB
#
##########################################################################


sub B_chgrp($$) {
   my ($newgown,$file_expr)=@_;
   my $oldown;
   my $oldgown;

   my $retval=1;

   my $file;
   my @files = glob($file_expr);

   foreach $file (@files) {

       # Prepend global prefix, but save original filename for &B_backup_file
       my $original_file=$file;

       $oldown=(stat $file)[4];
       $oldgown=(stat $file)[5];

       &B_log("ACTION", "Change group ownership on $original_file from $oldgown to $newgown\n");
       &B_log("ACTION", "chown $oldown,$newgown,\"$original_file\";\n");
       if ( -e $file ) {
           unless ($GLOBAL_LOGONLY) {
               # changing the group for the file/directory
               $retval = chown $oldown,$newgown,$file;
               if($retval){
                   # if the distribution is HP-UX then the modifications should
                   # also be made to the IPD (installed product database)
                   if(&GetDistro =~ "^HP-UX"){
                       &B_swmodify($file);
                   }
                   &B_revert_log(&getGlobal('BIN', "chgrp") . " $oldgown $file\n");
               }
           }
           unless ($retval) {
               &B_log("ERROR","Couldn't change ownership to $newgown on file $original_file\n");
           }
       }
       else {
           &B_log("ERROR","chgrp: File $original_file doesn't exist!\n");
           $retval=0;
       }
   }

   $retval;
}

###########################################################################
# &B_chgrp_link just like &B_chgrp but one exception:
# if the input file is a link
# it will not change the target's ownship, it only change the link itself's ownship
###########################################################################
sub B_chgrp_link($$) {
    my ($newgown,$file_expr)=@_;
    my $chgrp = &getGlobal("BIN","chgrp");
    my @files = glob($file_expr);
    my $retval=1;

    foreach my $file (@files) {
        # Prepend prefix, but save original filename
        my $original_file=$file;
        my $oldgown=(stat $file)[5];

        &B_log("ACTION","change group ownership on $original_file from $oldgown to $newgown\n");
        &B_log("ACTION","chgrp -h  $newgown \"$original_file\";\n");
        if ( -e $file ) {
            unless ($GLOBAL_LOGONLY) {
                # do not follow link with option -h
                `$chgrp -h $newgown $file`;
                $retval = ($? >> 8);
                if($retval == 0 ){
                    # if the distribution is HP-UX then the modifications should
                    # also be made to the IPD (installed product database)
                    if(&GetDistro =~ "^HP-UX"){
                        &B_swmodify($file);
                    }
                    # making ownership change revert-able
                    &B_revert_log("$chgrp" . " -h $oldgown $file\n");
                }
            }
            unless (! $retval) {
                &B_log("ERROR","Couldn't change group ownership to $newgown on file $original_file\n");
            }
        }
        else {
            &B_log("ERROR","chgrp: File $original_file doesn't exist!\n");
            $retval=0;
        }
    }
}

###########################################################################
# B_userdel($user) removes $user from the system, chmoding her home
# directory to 000, root:root owned, and removes the user from all
# /etc/passwd, /etc/shadow and /etc/group lines.
#
# In the future, we may also choose to make a B_lock_account routine.
#
# This routine depends on B_remove_user_from_group.
###########################################################################

sub B_userdel($) {

    my $user_to_remove = $_[0];

    if (&GetDistro =~ /^HP-UX/) {
        return 0;

        # Not yet suported on HP-UX, where we'd need to support
        # the TCB files and such.
    }

    #
    # First, let's chmod/chown/chgrp the user's home directory.
    #

    # Get the user's home directory from /etc/passwd
    if (open PASSWD,&getGlobal('FILE','passwd')) {
        my @lines=<PASSWD>;
        close PASSWD;

        # Get the home directory
        my $user_line = grep '^\s*$user_to_remove\s*:',@lines;
        my $home_directory = (split /\s*:\s*/,$user_line)[5];

        # Chmod that home dir to 0000,owned by uid 0, gid 0.
        if (&B_chmod_if_exists(0000,$home_directory)) {
            &B_chown(0,$home_directory);
            &B_chgrp(0,$home_directory);
        }
    }
    else {
        &B_log('ERROR',"B_userdel couldn't open the passwd file to remove a user.");
        return 0;
    }

    #
    # Next find out what groups the user is in, so we can call
    # B_remove_user_from_group($user,$group)
    #
    # TODO: add this to the helper functions for the test suite.
    #

    my @groups = ();

    # Parse /etc/group, looking for our user.
    if (open GROUP,&getGlobal('FILE','group')) {
        my @lines = <GROUP>;
        close GROUP;

        foreach my $line (@lines) {

            # Parse the line -- first field is group, last is users in group.
            if ($line =~ /([^\#^:]+):[^:]+:[^:]+:(.*)/) {
                my $group = $1;
                my $users_section = $2;

                # Get the user list and check if our user is in it.
                my @users = split /\s*,\s*/,$users_section;
                foreach my $user (@users) {
                    if ($user_to_remove eq $user) {
                        push @groups,$group;
                        last;
                    }
                }
            }
        }
    }

    # Now remove the user from each of those groups.
    foreach my $group (@groups) {
        &B_remove_user_from_group($user_to_remove,$group);
    }

    # Remove the user's /etc/passwd and /etc/shadow lines
    &B_delete_line(&getGlobal('FILE','passwd'),"^$user_to_remove\\s*:");
    &B_delete_line(&getGlobal('FILE','shadow'),"^$user_to_remove\\s*:");


    #
    # We should delete the user's group as well, if it's a single-user group.
    #
    if (open ETCGROUP,&getGlobal('FILE','group')) {
        my @group_lines = <ETCGROUP>;
        close ETCGROUP;
        chomp @group_lines;

        if (grep /^$user_to_remove\s*:[^:]*:[^:]*:\s*$/,@group_lines > 0) {
           &B_groupdel($user_to_remove);
        }
    }

}

###########################################################################
# B_groupdel($group) removes $group from /etc/group.
###########################################################################

sub B_groupdel($) {

    my $group = $_[0];

    # First read /etc/group to make sure the group is in there.
    if (open GROUP,&getGlobal('FILE','group')) {
        my @lines=<GROUP>;
        close GROUP;

        # Delete the line in /etc/group if present
        if (grep /^$group:/,@lines > 0) {
            # The group is named in /etc/group
            &B_delete_line(&getGlobal('FILE','group'),"^$group:/");
        }
    }

}


###########################################################################
# B_remove_user_from_group($user,$group) removes $user from $group,
# by modifying $group's /etc/group line, pulling the user out.  This
# uses B_chunk_replace thrice to replace these patterns:
#
#   ":\s*$user\s*," --> ":"
#   ",\s*$user" -> ""
#
###########################################################################

sub B_remove_user_from_group($$) {

    my ($user_to_remove,$group) = @_;

    #
    # We need to find the line from /etc/group that defines the group, parse
    # it, and put it back together without this user.
    #

    # Open the group file
    unless (open GROUP,&getGlobal('FILE','group')) {
        &B_log('ERROR',"&B_remove_user_from_group couldn't read /etc/group to remove $user_to_remove from $group.\n");
        return 0;
    }
    my @lines = <GROUP>;
    close GROUP;
    chomp @lines;

    #
    # Read through the lines to find the one we care about.  We'll construct a
    # replacement and then use B_replace_line to make the switch.
    #

    foreach my $line (@lines) {

        if ($line =~ /^\s*$group\s*:/) {

            # Parse this line.
            my @group_entries = split ':',$line;
            my @users = split ',',($group_entries[3]);

            # Now, recreate it.
            my $first_user = 1;
            my $group_line = $group_entries[0] . ':' . $group_entries[1] . ':' . $group_entries[2] . ':';

            # Add every user except the one we're removing.
            foreach my $user (@users) {

                # Remove whitespace.
                $user =~ s/\s+//g;

                if ($user ne $user_to_remove) {
                    # Add the user to the end of the line, prefacing
                    # it with a comma if it's not the first user.

                    if ($first_user) {
                        $group_line .= "$user";
                        $first_user = 0;
                    }
                    else {
                        $group_line .= ",$user";
                    }
                }
            }

            # The line is now finished.  Replace the original line.
            $group_line .= "\n";
            &B_replace_line(&getGlobal('FILE','group'),"^\\s*$group\\s*:",$group_line);
        }

    }
    return 1;
}

###########################################################################
# &B_check_owner_group($$$)
#
# Checks if the given file has the given owner and/or group.
# If the given owner is "", checks group only.
# If the given group is "", checks owner only.
#
# return values:
# 1: file has the given owner and/or group
#    or file exists, and both the given owner and group are ""
# 0: file does not has the given owner or group
#    or file does not exists
############################################################################

sub B_check_owner_group ($$$){
  my ($fileName, $owner, $group) = @_;

  if (-e $fileName) {
      my @junk=stat ($fileName);
      my $uid=$junk[4];
      my $gid=$junk[5];

      # Check file owner
      if ($owner ne "") {
          if (getpwnam($owner) != $uid) {
              return 0;
          }
      }

      # Check file group
      if ($group ne "") {
          if (getgrnam($group) != $gid) {
              return 0;
          }
      }

      return 1;
  }
  else {
      # Something is wrong if the file not exist
      return 0;
  }
}

##########################################################################
# this subroutine will test whether the given file is unowned
##########################################################################
sub B_is_unowned_file($) {
    my $file =$_;
    my $uid = (stat($file))[4];
    my $uname = (getpwuid($uid))[0];
    if ( $uname =~ /.+/ ) {
        return 1;
    }
    return 0;
}

##########################################################################
# this subroutine will test whether the given file is ungrouped
##########################################################################
sub B_is_ungrouped_file($){
    my $file =$_;
    my $gid = (stat($file))[5];
    my $gname = (getgrgid($gid))[0];
    if ( $gname =~ /.+/ ) {
        return 1;
    }
    return 0;
}




###########################################################################
# &B_check_permissions($$)
#
# Checks if the given file has the given permissions or stronger, where we
# define stronger as "less accessible."  The file argument must be fully
# qualified, i.e. contain the absolute path.
#
# return values:
# 1: file has the given permissions or better
# 0:  file does not have the given permsssions
# undef: file permissions cannot be determined
###########################################################################

sub B_check_permissions ($$){
  my ($fileName, $reqdPerms) = @_;
  my $filePerms;                        # actual permissions


  if (-e $fileName) {
    if (stat($fileName)) {
      $filePerms = (stat($fileName))[2] & 07777;
    }
    else {
      &B_log ("ERROR", "Can't stat $fileName.\n");
      return undef;
    }
  }
  else {
    # If the file does not exist, permissions are as good as they can get.
    return 1;
  }

  #
  # We can check whether the $filePerms are as strong by
  # bitwise ANDing them with $reqdPerms and checking if the
  # result is still equal to $filePerms.  If it is, the
  # $filePerms are strong enough.
  #
  if ( ($filePerms & $reqdPerms) == $filePerms ) {
      return 1;
  }
  else {
      return 0;
  }

}

##########################################################################
# B_permission_test($user, $previlege,$file)
# $user can be
# "owner"
# "group"
# "other"
# $previlege can be:
# "r"
# "w"
# "x"
# "suid"
# "sgid"
# "sticky"
# if previlege is set to suid or sgid or sticky, then $user can be empty
# this sub routine test whether the $user has the specified previlige to $file
##########################################################################

sub B_permission_test($$$){
    my ($user, $previlege, $file) = @_;

    if (-e $file ) {
        my $mode = (stat($file))[2];
        my $bitpos;
        # bitmap is | suid sgid sticky | rwx | rwx | rwx
        if ($previlege =~ /suid/ ) {
            $bitpos = 11;
        }
        elsif ($previlege =~ /sgid/ ) {
            $bitpos = 10;
        }
        elsif ($previlege =~ /sticky/ )  {
            $bitpos = 9;
        }
        else {
            if ( $user =~ /owner/) {
                if ($previlege =~ /r/) {
                    $bitpos = 8;
                }
                elsif ($previlege =~ /w/) {
                    $bitpos =7;
                }
                elsif ($previlege =~ /x/) {
                    $bitpos =6;
                }
                else {
                    return 0;
                }
            }
            elsif ( $user =~ /group/) {
                if ($previlege =~ /r/) {
                    $bitpos =5;
                }
                elsif ($previlege =~ /w/) {
                    $bitpos =4;
                }
                elsif ($previlege =~ /x/) {
                    $bitpos =3;
                }
                else {
                    return 0;
                }
            }
            elsif ( $user =~ /other/) {
                if ($previlege =~ /r/) {
                    $bitpos =2;
                }
                elsif ($previlege =~ /w/) {
                    $bitpos =1;
                }
                elsif ($previlege =~ /x/) {
                    $bitpos =0;
                }
                else {
                    return 0;
                }
            }
            else {
                return 0;
            }
        }
        $mode /= 2**$bitpos;
        if ($mode % 2) {
            return 1;
        }
        return 0;
    }
}

##########################################################################
# this subroutine will return a list of home directory
##########################################################################
sub B_find_homes(){
    # find loginable homes
    my $logins = &getGlobal("BIN","logins");
    my @lines = `$logins -ox`;
    my @homes;
    foreach my $line (@lines) {
        chomp $line;
        my @data = split /:/, $line;
        if ($data[7] =~ /PS/ && $data[5] =~ /home/) {
            push @homes, $data[5];
        }
    }
    return @homes;
}


###########################################################################
# B_is_executable($)
#
# This routine reports on whether a file is executable by the current
# process' effective UID.
#
# scalar return values:
# 0:     file is not executable
# 1:     file is executable
#
###########################################################################

sub B_is_executable($)
{
    my $name = shift;
    my $executable = 0;

    if (-x $name) {
        $executable = 1;
    }
    return $executable;
}

###########################################################################
# B_is_suid($)
#
# This routine reports on whether a file is Set-UID and owned by root.
#
# scalar return values:
# 0:     file is not SUID root
# 1:     file is SUID root
#
###########################################################################

sub B_is_suid($)
{
    my $name = shift;

    my @FileStatus = stat($name);
    my $IsSuid = 0;

    if (-u $name) #Checks existence and suid
    {
        if($FileStatus[4] == 0) {
            $IsSuid = 1;
        }
    }

    return $IsSuid;
}

###########################################################################
# B_is_sgid($)
#
# This routine reports on whether a file is SGID and group owned by
# group root (gid 0).
#
# scalar return values:
# 0:     file is not SGID root
# 1:     file is SGID root
#
###########################################################################

sub B_is_sgid($)
{
    my $name = shift;

    my @FileStatus = stat($name);
    my $IsSgid = 0;

    if (-g $name) #checks existence and sgid
    {
        if($FileStatus[5] == 0) {
            $IsSgid = 1;
        }
    }

    return $IsSgid;
}

###########################################################################
# B_get_user_list()
#
# This routine outputs a list of users on the system.
#
###########################################################################

sub B_get_user_list()
{
    my @users;
    open(PASSWD,&getGlobal('FILE','passwd'));
    while(<PASSWD>) {
        #Get the users
        if (/^([^:]+):/)
        {
            push (@users,$1);
        }
    }
     return @users;
}

###########################################################################
# B_get_group_list()
#
# This routine outputs a list of groups on the system.
#
###########################################################################

sub B_get_group_list()
{
    my @groups;
    open(GROUP,&getGlobal('FILE','group'));
    while(my $group_line = <GROUP>) {
        #Get the groups
        if ($group_line =~ /^([^:]+):/)
        {
            push (@groups,$1);
        }
    }
     return @groups;
}


###########################################################################
# &B_remove_suid ($file) removes the suid bit from $file if it
# is set and the file exist. If you would like to remove the suid bit
# from /bin/ping then you need to use:
#
#                 &B_remove_suid("/bin/ping");
#
# &B_remove_suid respects GLOBAL_LOGONLY.
# &B_remove_suid uses &B_chmod to make the permission changes
# &B_remove_suid allows for globbing.  tyler_e
#
###########################################################################

sub B_remove_suid($) {
   my $file_expr = $_[0];

   &B_log("ACTION","Removing SUID bit from \"$file_expr\".");
   unless ($GLOBAL_LOGONLY) {
       my @files = glob($file_expr);

     foreach my $file (@files) {
         # check file existence
         if(-e $file){
            # stat current file to get raw permissions
            my $old_perm_raw = (stat $file)[2];
            # test to see if suidbit is set
            my $suid_bit = (($old_perm_raw/2048) % 2);
            if($suid_bit == 1){
                # new permission without the suid bit
                my $new_perm = ((($old_perm_raw/512) % 8 ) - 4) .
                    (($old_perm_raw/64) % 8 ) .
                        (($old_perm_raw/8) % 8 ) .
                            (($old_perm_raw) % 8 );
                if(&B_chmod(oct($new_perm), $file)){
                    &B_log("ACTION","Removed SUID bit from \"$file\".");
                }
                else {
                    &B_log("ERROR","Could not remove SUID bit from \"$file\".");
                }
            } # No action if SUID bit is not set
        }# No action if file does not exist
      }# Repeat for each file in the file glob
    } # unless Global_log
}



1;

