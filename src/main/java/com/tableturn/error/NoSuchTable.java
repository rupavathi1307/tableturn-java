package com.tableturn.error;

/** 404 /errors/no-such-table. */
public class NoSuchTable extends DomainException {

  public NoSuchTable(String branch, String no) {
        super(404, "/errors/no-such-table", "No such table", "There is no table " + no + " in " + branch);
  }
}