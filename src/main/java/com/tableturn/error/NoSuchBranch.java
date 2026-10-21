package com.tableturn.error;

/** 404 /errors/no-such-branch. */
public class NoSuchBranch extends DomainException {

   public NoSuchBranch(String branch) {
        super(404, "/errors/no-such-branch", "No such branch", "There is no branch " + branch);
    }
}