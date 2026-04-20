# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when with code in this repository.

## Overview

A small flat-file CMS written in Java. Content is stored as plain-text files in a directory (no database). Users write markdown to create/edit pages, and pages are organized by tags. Authentication is cookie-based (username + MD5 password hash).

## Build & Run

```bash
# Build
mvn compile

# Run (requires JVM args for configuration)
mvn -Dport=8880 -Dusername=user -Dpassword=5ebe2294ecd0e0f08eab7690d2a6ee69 -Denable_cache=false -Dwebsite=localhost exec:java
```

JVM parameters (`-D` flags):
- `port` — server port
- `username` / `password` — credentials for editing (password is MD5 hash)
- `enable_cache` — toggle template caching
- `website` — site name displayed in pages
- `pages_path` — directory containing page files (default: `pages`)

Java 21 required (`maven.compiler.release=21`).

## Architecture

Built on **MVCly** (use the "use-mvcly" skill to understand the framework). Templates use **Apache Velocity** (`.vm` files in `templates/`). Markdown is converted to HTML via **Commonmark** with autolink, GFM tables, and image attributes extensions.

Base package: `net.jpkg.mtpages`

### Request Flow

`Main` bootstraps the app: it scans the pages directory into a shared `List<Page>`, registers all controllers with their URL patterns, and starts the MVCly HTTP server (Netty-based). Controllers extend either `BaseTemplateCtrl` (renders a `.vm` template) or `FormController` (processes POST and redirects).

### Page File Format

Each file in the pages directory is one CMS page. The format is three header lines followed by markdown content:
```
<title>
<space-separated tags>
public|private
<markdown content...>
```

### Controller Patterns

- **Template controllers** (`BaseTemplateCtrl` subclasses): Override `getContext(HttpRequest)` to return a `Map<String, Object>` passed to the Velocity template.
- **Form controllers** (`FormController` subclasses): Override `execute(HttpHeaders, HttpRequest, Map<String, String>)` to handle POST data. Use `setTarget()` for redirect destination.
- Dependency injection is manual: controllers needing the shared pages list have a `setPages(List<Page>)` method called by `ControllerFactory`.

### Authentication

`AuthValidator.isAuthenticated(HttpRequest)` checks a `sessionId` cookie against a UUID generated at startup. Controllers call this to gate edit/delete actions (throwing `ServiceException.Unauthorized()` when not authenticated).

## Packages

- `auth` — Cookie-based authentication check
- `core` — `Main` (bootstrap + route registration), `AppParameters` (JVM arg enum), `PagesScanner` (reads page files)
- `ctrl` — All web controllers (template and form)
- `mdl` — `Page` record (id, title, tags, public flag)
- `vm` — `TemplateUtil` (helper used in Velocity templates)
