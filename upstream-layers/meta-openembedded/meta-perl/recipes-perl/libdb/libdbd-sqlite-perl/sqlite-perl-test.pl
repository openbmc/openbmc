#! /usr/bin/env perl
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software Foundation.
#
# Copyright (C) 2013 Wind River Systems, Inc.
#
# - It tests DBI and DBD::SQLite could work correctly which means one could
#   manipulate sqlite database in perl
# - The test includes create/insert/update/delete/select, the five important
#   things one can do with a table
use DBI;

sub execute_sql {
    my $dbh = $_[0];
    my $sql = $_[1];
    my $sth = $dbh->prepare($sql)
        or die "Couldn't prepare statement: " . $dbh->errstr;
    $sth->execute();
    print "$sql\n";
    return $sth;
}

sub select_all {
    my $dbh = $_[0];
    my $table = $_[1];
    my $sth = &execute_sql($dbh, "Select * from $table");

    print "-----------------------------------\n";
    while (@data = $sth->fetchrow_array()) {
        my $name = $data[0];
        my $id = $data[1];
        print "$name: $id\n";
    }
    print "\n";

    $sth->finish;
    return $sth;
}

# A private, temporary in-memory database is created for the connection.
# This in-memory database will vanish when the database connection is
# closed. It is handy for your library tests.
my $dbfile = ":memory:";
my $dbh = DBI->connect("DBI:SQLite:dbname=$dbfile","","")
        or die "Couldn't connect to database: " . DBI->errstr;
print "Connect to SQLite's in-memory database\n";

&execute_sql($dbh, "Create table tbl1(name varchar(10), id smallint)");
&execute_sql($dbh, "Insert into tbl1 values('yocto',10)");
&execute_sql($dbh, "Insert into tbl1 values('windriver', 20)");
&select_all($dbh, "tbl1");

&execute_sql($dbh, "Update tbl1 set name = 'oe-core' where id = 10");
&execute_sql($dbh, "Delete from tbl1 where id = 20");
&select_all($dbh, "tbl1");

$dbh->disconnect;
print "Test Success\n"
