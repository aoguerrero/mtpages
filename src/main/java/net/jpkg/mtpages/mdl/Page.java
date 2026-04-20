package net.jpkg.mtpages.mdl;

import java.util.List;

public record Page(String id, String title, List<String> tags, boolean pblic) {

  public Page(String id) {
    this(id, null, null, false);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Page page) {
      return id.equals(page.id);
    }
    return false;
  }
}
