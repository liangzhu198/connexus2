package com.example.connexus2;

import java.util.Date;

import com.google.common.base.Joiner;

public class Stream implements Comparable<Stream> {

	public Long id;
	public String name;
	public String tags;
	public Date createDate;
	public String coverImageUrl;

	public Stream() {
	}
	
	public Stream(String name, String tags, String coverImageUrl) {
		this.name = name;
		this.tags = tags;
		this.coverImageUrl = coverImageUrl;
		this.createDate = new Date();
	}

	@Override
	public String toString() {
		Joiner joiner = Joiner.on(":");
		return joiner.join(id.toString(), name, tags, createDate.toString());
 	}

	@Override
	public int compareTo(Stream other) {
		if (createDate.after(other.createDate)) {
			return 1;
		} else if (createDate.before(other.createDate)) {
			return -1;
		}
		return 0;
	}
}
