package Bastille::API::FileContent;
use strict;

use Bastille::API;

require Exporter;
our @ISA = qw(Exporter);
our @EXPORT_OK = qw(
B_blank_file
B_insert_line_after
B_insert_line_before
B_insert_line
B_append_line
B_prepend_line
B_replace_line
B_replace_lines
B_replace_pattern
B_match_line
B_match_line_only
B_match_chunk
B_return_matched_lines
B_hash_comment_line
B_hash_uncomment_line
B_delete_line
B_chunk_replace
B_print
B_getValueFromFile
B_getValueFromString

B_TODO
B_TODOFlags
);
our @EXPORT = @EXPORT_OK;



###########################################################################
# &B_blank_file ($filename,$pattern) blanks the file $filename, unless the
# pattern $pattern is present in the file.  This lets us completely redo
# a file, if it isn't the one we put in place on a previous run...
#
# B_blank_file respects $GLOBAL_LOGONLY and uses B_open_plus and B_close_plus
# so that it makes backups and only modifies files when we're not in "-v"
# mode...
#
# If the file does not exist, the function does nothing, and gives an error
# to the Error Log
#
###########################################################################

sub B_blank_file($$) {

    my ($filename,$pattern) = @_;
    my $retval;

    # If this variable is true, we won't blank the file...

    my $found_pattern=0;

    if ($retval=&B_open_plus (*BLANK_NEW,*BLANK_OLD,$filename) ) {

        my @lines;

        while (my $line = <BLANK_OLD>) {

            push @lines,$line;
            if ($line =~ $pattern) {
                $found_pattern=1;
            }
        }

        # Only copy the old file if the new one didn't match.
        if ($found_pattern) {
            while ( my $line = shift @lines ) {
                &B_print(*BLANK_NEW,$line);
            }
        }
        else {
            &B_log("ACTION","Blanked file $filename\n");
        }
        &B_close_plus(*BLANK_NEW,*BLANK_OLD,$filename);
    }
    else {
        &B_log("ERROR","Couldn't blank file $filename since we couldn't open it or its replacement\n");
    }

    return $retval;

}

###########################################################################
# &B_insert_line_after ($filename,$pattern,$line_to_insert,$line_to_follow)
# modifies $filename, inserting $line_to_insert unless one or more lines
# in the file matches $pattern.  The $line_to_insert will be placed
# immediately after $line_to_follow, if it exists.  If said line does not
# exist, the line will not be inserted and this routine will return 0.
#
# B_insert_line uses B_open_plus and B_close_plus, so that the file
# modified is backed up...
#
# Here's examples of where you might use this:
#
# You'd like to insert a line in Apache's configuration file, in a
# particular section.
#
###########################################################################

sub B_insert_line_after($$$$) {

    my ($filename,$pattern,$line_to_insert,$line_to_follow) = @_;

    my @lines;
    my $found_pattern=0;
    my $found_line_to_follow=0;

    my $retval=1;

    if ( &B_open_plus (*INSERT_NEW,*INSERT_OLD,$filename) ) {

        # Read through the file looking for a match both on the $pattern
        # and the line we are supposed to be inserting after...

        my $ctr=1;
        while (my $line=<INSERT_OLD>) {
            push (@lines,$line);
            if ($line =~ $pattern) {
                $found_pattern=1;
            }
            if ( ($found_line_to_follow < 1) and ($line =~ $line_to_follow)) {
                $found_line_to_follow=$ctr;
            }
            $ctr++;
        }

        # Log an error if we never found the line we were to insert after
        unless ($found_line_to_follow ) {
            $retval=0;
            &B_log("ERROR","Never found the line that we were supposed to insert after in $filename\n");
        }

        # Now print the file back out, inserting our line if we should...

        $ctr=1;
        while (my $line = shift @lines) {
            &B_print(*INSERT_NEW,$line);
            if ( ($ctr == $found_line_to_follow) and ($found_pattern == 0) ) {
                &B_print(*INSERT_NEW,$line_to_insert);
                &B_log("ACTION","Inserted the following line in $filename:\n");
                &B_log("ACTION","$line_to_insert");
            }
            $ctr++;
        }

        &B_close_plus (*INSERT_NEW,*INSERT_OLD,$filename);

    }
    else {
        $retval=0;
        &B_log("ERROR","Couldn't insert line to $filename, since open failed.");
    }

    return $retval;

}
###########################################################################
# &B_insert_line_before ($filename,$pattern,$line_to_insert,$line_to_preceed)
# modifies $filename, inserting $line_to_insert unless one or more lines
# in the file matches $pattern.  The $line_to_insert will be placed
# immediately before $line_to_preceed, if it exists.  If said line does not
# exist, the line will not be inserted and this routine will return 0.
#
# B_insert_line uses B_open_plus and B_close_plus, so that the file
# modified is backed up...
#
# Here's examples of where you might use this:
#
# You'd like to insert a line in Apache's configuration file, in a
# particular section.
#
###########################################################################

sub B_insert_line_before($$$$) {

    my ($filename,$pattern,$line_to_insert,$line_to_preceed) = @_;

    my @lines;
    my $found_pattern=0;
    my $found_line_to_preceed=0;

    my $retval=1;

    if ( &B_open_plus (*INSERT_NEW,*INSERT_OLD,$filename) ) {

        # Read through the file looking for a match both on the $pattern
        # and the line we are supposed to be inserting after...

        my $ctr=1;
        while (my $line=<INSERT_OLD>) {
            push (@lines,$line);
            if ($line =~ $pattern) {
                $found_pattern=1;
            }
            if ( ($found_line_to_preceed < 1) and ($line =~ $line_to_preceed)) {
                $found_line_to_preceed=$ctr;
            }
            $ctr++;
        }

        # Log an error if we never found the line we were to preceed
        unless ($found_line_to_preceed ) {
            $retval=0;
            &B_log("ERROR","Never found the line that we were supposed to insert before in $filename\n");
        }

        # Now print the file back out, inserting our line if we should...

        $ctr=1;
        while (my $line = shift @lines) {
            if ( ($ctr == $found_line_to_preceed) and ($found_pattern == 0) ) {
                &B_print(*INSERT_NEW,$line_to_insert);
                &B_log("ACTION","Inserted the following line in $filename:\n");
                &B_log("ACTION","$line_to_insert");
            }
            &B_print(*INSERT_NEW,$line);
            $ctr++;
        }

        &B_close_plus (*INSERT_NEW,*INSERT_OLD,$filename);

    }
    else {
        $retval=0;
        &B_log("ERROR","Couldn't insert line to $filename, since open failed.");
    }

    return $retval;

}

###########################################################################
# &B_insert_line ($filename,$pattern,$line_to_insert,$line_to_follow)
#
#   has been renamed to B_insert_line_after()
#
# This name will continue to work, as a shim for code that has not been
# transitioned.
###########################################################################

sub B_insert_line($$$$) {

    my $rtn_value = &B_insert_line_after(@_);

    return ($rtn_value);
}


###########################################################################
# &B_append_line ($filename,$pattern,$line_to_append)  modifies $filename,
# appending $line_to_append unless one or more lines in the file matches
# $pattern.  This is an enhancement to the append_line_if_no_such_line_exists
# idea.
#
# Additionally, if $pattern is set equal to "", the line is always appended.
#
# B_append_line uses B_open_plus and B_close_plus, so that the file
# modified is backed up...
#
# Here's examples of where you might use this:
#
# You'd like to add a   root   line to /etc/ftpusers if none exists.
# You'd like to add a   Options Indexes  line to Apache's config. file,
# after you delete all Options lines from said config file.
#
###########################################################################

sub B_append_line($$$) {

    my ($filename,$pattern,$line_to_append) = @_;

    my $found_pattern=0;
    my $retval=1;

    if ( &B_open_plus (*APPEND_NEW,*APPEND_OLD,$filename) ) {
        while (my $line=<APPEND_OLD>) {
            &B_print(*APPEND_NEW,$line);
            if ($line =~ $pattern) {
                $found_pattern=1;
            }
        }
        # Changed != 0 to $pattern so that "" works instead of 0 and perl
        # does not give the annoying
        # Argument "XX" isn't numeric in ne at ...
        if ( $pattern eq "" or ! $found_pattern ) {
            &B_print(*APPEND_NEW,$line_to_append);
            &B_log("ACTION","Appended the following line to $filename:\n");
            &B_log("ACTION","$line_to_append");
        }
        &B_close_plus (*APPEND_NEW,*APPEND_OLD,$filename);
    }
    else {
        $retval=0;
        &B_log("ERROR","# Couldn't append line to $filename, since open failed.");
    }

    return $retval;

}

###########################################################################
# &B_prepend_line ($filename,$pattern,$line_to_prepend)  modifies $filename,
# pre-pending $line_to_prepend unless one or more lines in the file matches
# $pattern.  This is an enhancement to the prepend_line_if_no_such_line_exists
# idea.
#
# B_prepend_line uses B_open_plus and B_close_plus, so that the file
# modified is backed up...
#
# Here's examples of where you might use this:
#
# You'd like to insert the line "auth   required   pam_deny.so" to the top
# of the PAM stack file /etc/pam.d/rsh to totally deactivate rsh.
#
###########################################################################

sub B_prepend_line($$$) {

    my ($filename,$pattern,$line_to_prepend) = @_;

    my @lines;
    my $found_pattern=0;
    my $retval=1;

    if ( &B_open_plus (*PREPEND_NEW,*PREPEND_OLD,$filename) ) {
        while (my $line=<PREPEND_OLD>) {
            push (@lines,$line);
            if ($line =~ $pattern) {
                $found_pattern=1;
            }
        }
        unless ($found_pattern) {
            &B_print(*PREPEND_NEW,$line_to_prepend);
        }
        while (my $line = shift @lines) {
            &B_print(*PREPEND_NEW,$line);
        }

        &B_close_plus (*PREPEND_NEW,*PREPEND_OLD,$filename);

        # Log the action
        &B_log("ACTION","Pre-pended the following line to $filename:\n");
        &B_log("ACTION","$line_to_prepend");
    }
    else {
        $retval=0;
        &B_log("ERROR","Couldn't prepend line to $filename, since open failed.\n");
    }

    return $retval;

}


###########################################################################
# &B_replace_line ($filename,$pattern,$line_to_switch_in) modifies $filename,
# replacing any lines matching $pattern with $line_to_switch_in.
#
# It returns the number of lines it replaced (or would have replaced, if
# LOGONLY mode wasn't on...)
#
# B_replace_line uses B_open_plus and B_close_plus, so that the file
# modified is backed up...
#
# Here an example of where you might use this:
#
# You'd like to replace any Options lines in Apache's config file with:
#            Options Indexes FollowSymLinks
#
###########################################################################

sub B_replace_line($$$) {

    my ($filename,$pattern,$line_to_switch_in) = @_;
    my $retval=0;

    if ( &B_open_plus (*REPLACE_NEW,*REPLACE_OLD,$filename) ) {
        while (my $line=<REPLACE_OLD>) {
            unless ($line =~ $pattern) {
                &B_print(*REPLACE_NEW,$line);
            }
            else {
                # Don't replace the line if it's already there.
                unless ($line eq $line_to_switch_in) {
                    &B_print(*REPLACE_NEW,$line_to_switch_in);

                    $retval++;
                    &B_log("ACTION","File modification in $filename -- replaced line\n" .
                           "$line\n" .
                           "with:\n" .
                           "$line_to_switch_in");
                }
                # But if it is there, make sure it stays there! (by Paul Allen)
                else {
                    &B_print(*REPLACE_NEW,$line);
                }
            }
        }
        &B_close_plus (*REPLACE_NEW,*REPLACE_OLD,$filename);
    }
    else {
        $retval=0;
        &B_log("ERROR","Couldn't replace line(s) in $filename because open failed.\n");
    }

    return $retval;
}

###########################################################################
# &B_replace_lines ($filename,$patterns_and_substitutes) modifies $filename,
# replacing the line matching the nth $pattern specified in $patterns_and_substitutes->[n]->[0]
# with the corresponding substitutes in $patterns_and_substitutes->[n]->-[1]
#
# It returns the number of lines it replaced (or would have replaced, if
# LOGONLY mode wasn't on...)
#
# B_replace_lines uses B_open_plus and B_close_plus, so that the file
# modified is backed up...
#
# Here an example of where you might use this:
#
# You'd like to replace /etc/opt/ssh/sshd_config file
# (^#|^)Protocol\s+(.*)\s*$                             ==>                Protocol 2
# (^#|^)X11Forwarding\s+(.*)\s*$                  ==>                X11Forwarding yes
# (^#|^)IgnoreRhosts\s+(.*)\s*$                     ==>                gnoreRhosts yes
# (^#|^)RhostsAuthentication\s+(.*)\s*$         ==>                RhostsAuthentication no
# (^#|^)RhostsRSAAuthentication\s+(.*)\s*$   ==>               RhostsRSAAuthentication no
# (^#|^)PermitRootLogin\s+(.*)\s*$                 ==>              PermitRootLogin no
# (^#|^)PermitEmptyPasswords\s+(.*)\s*$      ==>              PermitEmptyPasswords no
# my $patterns_and_substitutes = [
#           [ '(^#|^)Protocol\s+(.*)\s*$'                             =>                'Protocol 2'],
#           ['(^#|^)X11Forwarding\s+(.*)\s*$'                  =>                'X11Forwarding yes'],
#           ['(^#|^)IgnoreRhosts\s+(.*)\s*$'                     =>                'gnoreRhosts yes'],
#           ['(^#|^)RhostsAuthentication\s+(.*)\s*$'         =>                'RhostsAuthentication no'],
#           ['(^#|^)RhostsRSAAuthentication\s+(.*)\s*$'   =>               'RhostsRSAAuthentication no'],
#           ['(^#|^)PermitRootLogin\s+(.*)\s*$'                 =>              'PermitRootLogin no'],
#          ['(^#|^)PermitEmptyPasswords\s+(.*)\s*$'      =>              'PermitEmptyPasswords no']
#]
# B_replaces_lines($sshd_config,$patterns_and_substitutes);
###########################################################################

sub B_replace_lines($$){
    my ($filename, $pairs) = @_;
    my $retval = 0;
    if ( &B_open_plus (*REPLACE_NEW,*REPLACE_OLD,$filename) ) {
        while (my $line = <REPLACE_OLD>) {
            my $switch;
            my $switch_before = $line;
            chomp($line);
            foreach my $pair (@$pairs) {
                $switch = 0;
               
                my $pattern = $pair->[0] ;
                my $replace = $pair->[1];
                my $evalstr = '$line'  . "=~ s/$pattern/$replace/";
                eval $evalstr;
                if ($@) {
                    &B_log("ERROR", "eval $evalstr failed.\n");
                }
                #if ( $line =~ s/$pair->[0]/$pair->[1]/) {
                #    $switch = 1;
                #    last;
                #}
            }
            &B_print(*REPLACE_NEW,"$line\n");
            if ($switch) {
                $retval++;
                B_log("ACTION","File modification in $filename -- replaced line\n" .
                      "$switch_before\n" .
                      "with:\n" .
                      "$line\n");
            }
        }
        &B_close_plus (*REPLACE_NEW,*REPLACE_OLD,$filename);
        return 1;
    }
    else {
        $retval=0;
        &B_log("ERROR","Couldn't replace line(s) in $filename because open failed.\n");
    }
}

################################################################################################
# &B_replace_pattern ($filename,$pattern,$pattern_to_remove,$text_to_switch_in)
# modifies $filename, acting on only lines that match $pattern, replacing a
# string that matches $pattern_to_remove with $text_to_switch_in.
#
# Ex:
#  B_replace_pattern('/etc/httpd.conf','^\s*Options.*\bIncludes\b','Includes','IncludesNoExec')
#
#   replaces all "Includes" with "IncludesNoExec" on Apache Options lines.
#
# It returns the number of lines it altered (or would have replaced, if
# LOGONLY mode wasn't on...)
#
# B_replace_pattern uses B_open_plus and B_close_plus, so that the file
# modified is backed up...
#
#################################################################################################

sub B_replace_pattern($$$$) {

    my ($filename,$pattern,$pattern_to_remove,$text_to_switch_in) = @_;
    my $retval=0;

    if ( &B_open_plus (*REPLACE_NEW,*REPLACE_OLD,$filename) ) {
        while (my $line=<REPLACE_OLD>) {
            unless ($line =~ $pattern) {
                &B_print(*REPLACE_NEW,$line);
            }
            else {
                my $orig_line =$line;
                $line =~ s/$pattern_to_remove/$text_to_switch_in/;

                &B_print(*REPLACE_NEW,$line);

                $retval++;
                &B_log("ACTION","File modification in $filename -- replaced line\n" .
                       "$orig_line\n" .
                       "via pattern with:\n" .
                       "$line\n\n");
            }
        }
        &B_close_plus (*REPLACE_NEW,*REPLACE_OLD,$filename);
    }
    else {
        $retval=0;
        &B_log("ERROR","Couldn't pattern-replace line(s) in $filename because open failed.\n");
    }

    return $retval;
}


###########################################################################
# &B_match_line($file,$pattern);
#
# This subroutine will return a 1 if the pattern specified can be matched
# against the file specified.  It will return a 0 otherwise.
#
# return values:
# 0:     pattern not in file or the file is not readable
# 1:     pattern is in file
###########################################################################
sub B_match_line($$) {
    # file to be checked and pattern to check for.
    my ($file,$pattern) = @_;
    # if the file is readable then
    if(-r $file) {
        # if the file can be opened then
        if(open FILE,"<$file") {
            # look at each line in the file
            while (my $line = <FILE>) {
                # if a line matches the pattern provided then
                if($line =~ $pattern) {
                    # return the pattern was found
                    B_log('DEBUG','Pattern: ' . $pattern . ' matched in file: ' .
                    $file . "\n");
                    return 1;
                }
            }
        }
        # if the file cann't be opened then
        else {
            # send a note to that affect to the errorlog
            &B_log("ERROR","Unable to open file for read.\n$file\n$!\n");
        }
    }
    B_log('DEBUG','Pattern: ' . $pattern . ' not matched in file: ' .
          $file . "\n");
    # the provided pattern was not matched against a line in the file
    return 0;
}

###########################################################################
# &B_match_line_only($file,$pattern);
#
# This subroutine checks if the specified pattern can be matched and if
# it's the only content in the file. The only content means it's only but
# may have several copies in the file.
#
# return values:
# 0:     pattern not in file or pattern is not the only content
#        or the file is not readable
# 1:     pattern is in file and it's the only content
############################################################################
sub B_match_line_only($$) {
    my ($file,$pattern) = @_;

    # if matched, set to 1 later
    my $retval = 0;

    # if the file is readable then
    if(-r $file) {
        # if the file can be opened then
        if(&B_open(*FILED, $file)) {
            # pattern should be matched at least once
            # pattern can not be mismatched
            while (my $line = <FILED>) {
                if ($line =~ $pattern) {
                    $retval = 1;
                }
                else {
                    &B_close(*FILED);
                    return 0;
                }
            }
        }
        &B_close(*FILED);
    }

    return $retval;
}

###########################################################################
# &B_return_matched_lines($file,$pattern);
#
# This subroutine returns lines in a file matching a given regular
# expression, when called in the default list mode.  When called in scalar
# mode, returns the number of elements found.
###########################################################################
sub B_return_matched_lines($$)
{
    my ($filename,$pattern) = @_;
    my @lines = ();

    open(READFILE, $filename);
    while (<READFILE>) {
        chomp;
        next unless /$pattern/;
        push(@lines, $_);
    }
    if (wantarray)
    {
        return @lines;
    }
    else
    {
        return scalar (@lines);
    }
}

###########################################################################
# &B_match_chunk($file,$pattern);
#
# This subroutine will return a 1 if the pattern specified can be matched
# against the file specified on a line-agnostic form.  This allows for
# patterns which by necessity must match against a multi-line pattern.
# This is the natural analogue to B_replace_chunk, which was created to
# provide multi-line capability not provided by B_replace_line.
#
# return values:
# 0:     pattern not in file or the file is not readable
# 1:     pattern is in file
###########################################################################

sub B_match_chunk($$) {

    my ($file,$pattern) = @_;
    my @lines;
    my $big_long_line;
    my $retval=1;

    open CHUNK_FILE,$file;

    # Read all lines into one scalar.
    @lines = <CHUNK_FILE>;
    close CHUNK_FILE;

    foreach my $line ( @lines ) {
        $big_long_line .= $line;
    }

    # Substitution routines get weird unless last line is terminated with \n
    chomp $big_long_line;
    $big_long_line .= "\n";

    # Exit if we don't find a match
    unless ($big_long_line =~ $pattern) {
        $retval = 0;
    }

    return $retval;
}

###########################################################################
# &B_hash_comment_line ($filename,$pattern) modifies $filename, replacing
# any lines matching $pattern with a "hash-commented" version, like this:
#
#
#        finger  stream  tcp     nowait  nobody  /usr/sbin/tcpd  in.fingerd
# becomes:
#        #finger  stream  tcp     nowait  nobody  /usr/sbin/tcpd  in.fingerd
#
# Also:
#       tftp        dgram  udp wait   root /usr/lbin/tftpd    tftpd\
#        /opt/ignite\
#        /var/opt/ignite
# becomes:
#       #tftp        dgram  udp wait   root /usr/lbin/tftpd    tftpd\
#       # /opt/ignite\
#       # /var/opt/ignite
#
#
# B_hash_comment_line uses B_open_plus and B_close_plus, so that the file
# modified is backed up...
#
###########################################################################

sub B_hash_comment_line($$) {

    my ($filename,$pattern) = @_;
    my $retval=1;

    if ( &B_open_plus (*HASH_NEW,*HASH_OLD,$filename) ) {
        my $line;
        while ($line=<HASH_OLD>) {
            unless ( ($line =~ $pattern) and ($line !~ /^\s*\#/) ) {
                &B_print(*HASH_NEW,$line);
            }
            else {
                &B_print(*HASH_NEW,"#$line");
                &B_log("ACTION","File modification in $filename -- hash commented line\n" .
                       "$line\n" .
                       "like this:\n" .
                       "#$line\n\n");
                # while the line has a trailing \ then we should also comment out the line below
                while($line =~ m/\\\n$/) {
                    if($line=<HASH_OLD>) {
                        &B_print(*HASH_NEW,"#$line");
                        &B_log("ACTION","File modification in $filename -- hash commented line\n" .
                               "$line\n" .
                               "like this:\n" .
                               "#$line\n\n");
                    }
                    else {
                        $line = "";
                    }
                }

            }
        }
        &B_close_plus (*HASH_NEW,*HASH_OLD,$filename);
    }
    else {
        $retval=0;
        &B_log("ERROR","Couldn't hash-comment line(s) in $filename because open failed.\n");
    }

    return $retval;
}


###########################################################################
# &B_hash_uncomment_line ($filename,$pattern) modifies $filename,
# removing any commenting from lines that match $pattern.
#
#        #finger  stream  tcp     nowait  nobody  /usr/sbin/tcpd  in.fingerd
# becomes:
#        finger  stream  tcp     nowait  nobody  /usr/sbin/tcpd  in.fingerd
#
#
# B_hash_uncomment_line uses B_open_plus and B_close_plus, so that the file
# modified is backed up...
#
###########################################################################

sub B_hash_uncomment_line($$) {

    my ($filename,$pattern) = @_;
    my $retval=1;

    if ( &B_open_plus (*HASH_NEW,*HASH_OLD,$filename) ) {
      my $line;
        while ($line=<HASH_OLD>) {
            unless ( ($line =~ $pattern) and ($line =~ /^\s*\#/) ) {
                &B_print(*HASH_NEW,$line);
            }
            else {
                $line =~ /^\s*\#+(.*)$/;
                $line = "$1\n";

                &B_print(*HASH_NEW,"$line");
                &B_log("ACTION","File modification in $filename -- hash uncommented line\n");
                &B_log("ACTION",$line);
                # while the line has a trailing \ then we should also uncomment out the line below
                while($line =~ m/\\\n$/) {
                    if($line=<HASH_OLD>) {
                        $line =~ /^\s*\#+(.*)$/;
                        $line = "$1\n";
                        &B_print(*HASH_NEW,"$line");
                        &B_log("ACTION","File modification in $filename -- hash uncommented line\n");
                        &B_log("ACTION","#$line");
                        &B_log("ACTION","like this:\n");
                        &B_log("ACTION","$line");
                    }
                    else {
                        $line = "";
                    }
                }
            }
        }
        &B_close_plus (*HASH_NEW,*HASH_OLD,$filename);
    }
    else {
        $retval=0;
        &B_log("ERROR","Couldn't hash-uncomment line(s) in $filename because open failed.\n");
    }

    return $retval;
}



###########################################################################
# &B_delete_line ($filename,$pattern) modifies $filename, deleting any
# lines matching $pattern.  It uses B_replace_line to do this.
#
# B_replace_line uses B_open_plus and B_close_plus, so that the file
# modified is backed up...
#
# Here an example of where you might use this:
#
# You'd like to remove any timeout=  lines in /etc/lilo.conf, so that your
# delay=1 modification will work.

#
###########################################################################


sub B_delete_line($$) {

    my ($filename,$pattern)=@_;
    my $retval=&B_replace_line($filename,$pattern,"");

    return $retval;
}


###########################################################################
# &B_chunk_replace ($file,$pattern,$replacement) reads $file replacing the
# first occurrence of $pattern with $replacement.
#
###########################################################################

sub B_chunk_replace($$$) {

    my ($file,$pattern,$replacement) = @_;

    my @lines;
    my $big_long_line;
    my $retval=1;

    &B_open (*OLDFILE,$file);

    # Read all lines into one scalar.
    @lines = <OLDFILE>;
    &B_close (*OLDFILE);
    foreach my $line ( @lines ) {
        $big_long_line .= $line;
    }

    # Substitution routines get weird unless last line is terminated with \n
    chomp $big_long_line;
    $big_long_line .= "\n";

    # Exit if we don't find a match
    unless ($big_long_line =~ $pattern) {
        return 0;
    }

    $big_long_line =~ s/$pattern/$replacement/s;

    $retval=&B_open_plus (*NEWFILE,*OLDFILE,$file);
    if ($retval) {
        &B_print (*NEWFILE,$big_long_line);
        &B_close_plus (*NEWFILE,*OLDFILE,$file);
    }

    return $retval;
}

###########################################################################
# &B_print ($handle,@list) prints the items of @list to the file handle
# $handle.  It logs the action and respects the $GLOBAL_LOGONLY variable.
#
###########################################################################

sub B_print {
   my $handle=shift @_;

   my $result=1;

   unless ($GLOBAL_LOGONLY) {
       $result=print $handle @_;
   }

   ($handle) = "$handle" =~ /[^:]+::[^:]+::([^:]+)/;

   $result;
}


##########################################################################
# &B_getValueFromFile($regex,$file);
# Takes a regex with a single group "()" and returns the unique value
# on any non-commented lines
# This (and B_return_matched_lines are only used in this file, though are
# probably more generally useful.  For now, leaving these here serve the following
#functions:
# a) still gets exported/associated as part of the Test_API package, and
# is still availble for a couple operations that can't be deferred to the
# main test loop, as they save values so that individual tests don't have to
# recreate  (copy / paste) the logic to get them.
#
# It also avoids the circular "use" if we incldued "use Test API" at the top
# of this file (Test API "uses" this file.
# Returns the uncommented, unique values of a param=value pair.
#
# Return values:
# 'Not Defined' if the value is not present or not uniquely defined.
# $value if the value is present and unique
#
###########################################################################
sub B_getValueFromFile ($$){
  my $inputRegex=$_[0];
  my $file=$_[1];
  my ($lastvalue,$value)='';

  my @lines=&B_return_matched_lines($file, $inputRegex);

  return &B_getValueFromString($inputRegex,join('/n',@lines));
}

##########################################################################
# &B_getValueFromString($param,$string);
# Takes a regex with a single group "()" and returns the unique value
# on any non-commented lines
# This (and B_return_matched_lines are only used in this file, though are
# probably more generally useful.  For now, leaving these here serve the following
#functions:
# a) still gets exported/associated as part of the Test_API package, and
# is still availble for a couple operations that can't be deferred to the
# main test loop, as they save values so that individual tests don't have to
# recreate  (copy / paste) the logic to get them.
#
# It also avoids the circular "use" if we incldued "use Test API" at the top
# of this file (Test API "uses" this file.
# Returns the uncommented, unique values of a param=value pair.
#
# Return values:
# 'Not Unique' if the value is not uniquely defined.
# undef if the value isn't defined at all
# $value if the value is present and unique
#
###########################################################################
sub B_getValueFromString ($$){
  my $inputRegex=$_[0];
  my $inputString=$_[1];
  my $lastValue='';
  my $value='';

  my @lines=split(/\n/,$inputString);

  &B_log("DEBUG","B_getvaluefromstring called with regex: $inputRegex and input: " .
         $inputString);
  foreach my $line (grep(/$inputRegex/,@lines)) {
    $line =~ /$inputRegex/;
    $value=$1;
    if (($lastValue eq '') and ($value ne '')) {
        $lastValue = $value;
    } elsif (($lastValue ne $value) and ($value ne '')) {
        B_log("DEBUG","getvaluefromstring returned Not Unique");
        return 'Not Unique';
    }
  }
  if ((not(defined($value))) or ($value eq '')) {
    &B_log("DEBUG","Could not find regex match in string");
    return undef;
  } else {
    &B_log("DEBUG","B_getValueFromString Found: $value ; using:  $inputRegex");
    return $value;
  }
}

###############################################################
# This function adds something to the To Do List.
# Arguments:
# 1) The string you want to add to the To Do List.
# 2) Optional: Question whose TODOFlag should be set to indicate
#    A pending manual action in subsequent reports.  Only skip this
#    If there's no security-audit relevant action you need the user to
#    accomplish
# Ex:
# &B_TODO("------\nInstalling IPFilter\n----\nGo get Ipfilter","IPFilter.install_ipfilter");
#
#
# Returns:
# 0 - If error condition
# True, if sucess, specifically:
#   "appended" if the append operation was successful
#   "exists" if no change was made since the entry was already present
###############################################################
sub B_TODO ($;$) {
    my $text = $_[0];
    my $FlaggedQuestion = $_[1];
    my $multilineString = "";

    # trim off any leading and trailing new lines, regexes separated for "clarity"
    $text =~ s/^\n+(.*)/$1/;
    $text =~ s/(.*)\n+$/$1/;

    if ( ! -e &getGlobal('BFILE',"TODO") ) {
	# Make the TODO list file for HP-UX Distro
	&B_create_file(&getGlobal('BFILE', "TODO"));
	&B_append_line(&getGlobal('BFILE', "TODO"),'a$b',
          "Please take the steps below to make your system more secure,\n".
          "then delete the item from this file and record what you did along\n".
          "with the date and time in your system administration log.  You\n".
          "will need that information in case you ever need to revert your\n".
          "changes.\n\n");
    }


    if (open(TODO,"<" . &getGlobal('BFILE', "TODO"))) {
	while (my $line = <TODO>) {
	    # getting rid of all meta characters.
	    $line =~ s/(\\|\||\(|\)|\[|\]|\{|\}|\^|\$|\*|\+|\?|\.)//g;
	    $multilineString .= $line;
	}
	chomp $multilineString;
        $multilineString .= "\n";

	close(TODO);
    }
    else {
	&B_log("ERROR","Unable to read TODO.txt file.\n" .
		  "The following text could not be appended to the TODO list:\n" .
		  $text .
		  "End of TODO text\n");
        return 0; #False
    }

    my $textPattern = $text;

    # getting rid of all meta characters.
    $textPattern =~ s/(\\|\||\(|\)|\[|\]|\{|\}|\^|\$|\*|\+|\?|\.)//g;

    if( $multilineString !~  "$textPattern") {
	my $datestamp = "{" . localtime() . "}";
	unless ( &B_append_line(&getGlobal('BFILE', "TODO"), "", $datestamp . "\n" . $text . "\n\n\n") ) {
	    &B_log("ERROR","TODO Failed for text: " . $text );
	}
        #Note that we only set the flag on the *initial* entry in the TODO File
        #Not on subsequent detection.  This is to avoid the case where Bastille
        #complains on a subsequent Bastille run of an already-performed manual
        #action that the user neglected to delete from the TODO file.
        # It does, however lead to a report of "nonsecure" when the user
        #asked for the TODO item, performed it, Bastille detected that and cleared the
        # Item, and then the user unperformed the action.  I think this is proper behavior.
        # rwf 06/06

        if (defined($FlaggedQuestion)) {
            &B_TODOFlags("set",$FlaggedQuestion);
        }
        return "appended"; #evals to true, and also notes what happened
    } else {
        return "exists"; #evals to true, and also
    }

}


#####################################################################
# &B_TODOFlags()
#
# This is the interface to the TODO flags.  Test functions set these when they
# require a TODO item to be completed to get to a "secure" state.
# The prune/reporting function checks these to ensure no flags are set before
# reporting an item "secure"
# "Methods" are load | save | isSet <Question> | set <Question> | unset <Question>
#
######################################################################

sub B_TODOFlags($;$) {
    my $action = $_[0];
    my $module = $_[1];

    use File::Spec;

    my $todo_flag = &getGlobal("BFILE","TODOFlag");

    &B_log("DEBUG","B_TODOFlags action: $action , module: $module");

    if ($action eq "load") {
	if (-e $todo_flag ) {
	    &B_open(*TODO_FLAGS, $todo_flag);
	    my @lines = <TODO_FLAGS>;
	    foreach my $line (@lines) {
                chomp($line);
		$GLOBAL_CONFIG{"$line"}{"TODOFlag"}="yes";
	    }
	    return (&B_close(*TODO_FLAGS)); #return success of final close
	} else {
            return 1; #No-op is okay
        }
    } elsif ($action eq "save") {
	# Make sure the file exists, else create
        #Note we use open_plus and and create file, so if Bastille is
        #reverted, all the flags will self-clear (file deleted)
        my $flagNumber = 0;
        my $flagData = '';
        foreach my $key (keys %GLOBAL_CONFIG) {
            if ($GLOBAL_CONFIG{$key}{"TODOFlag"} eq "yes") {
                ++$flagNumber;
                $flagData .= "$key\n";
	    }
	}
        if (not( -e $todo_flag)) {
                &B_log("DEBUG","Initializing TODO Flag file: $todo_flag");
                &B_create_file($todo_flag); # Make sure it exists
        }
        &B_blank_file($todo_flag,
                          "This will not appear in the file; ensures blanking");
        return &B_append_line($todo_flag, "", "$flagData"); #return success of save
    } elsif (($action eq "isSet") and ($module ne "")) {
	if ($GLOBAL_CONFIG{"$module"}{"TODOFlag"} eq "yes") {
	    return 1; #TRUE
	} else {
	    return 0; #FALSE
        }
    } elsif (($action eq "set") and ($module ne "")) {
        $GLOBAL_CONFIG{"$module"}{"TODOFlag"} = "yes";
    } elsif (($action eq "clear") and ($module ne "")) {
        $GLOBAL_CONFIG{"$module"}{"TODOFlag"} = "";
    } else {
	&B_log("ERROR","TODO_Flag Called with invalid parameters: $action , $module".
	       "audit report may be incorrect.");
	return 0; #FALSE
    }
}

1;


