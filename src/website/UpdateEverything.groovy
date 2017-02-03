package website

import java.util.regex.Matcher

import static groovy.io.FileType.FILES

class UpdateEverything {

    static final String newHeader = new File('./templates/header.html').text
    static final String newFooter = new File('./templates/footer.html').text

    static void main(String... args) {
        update('<header', '</header>', newHeader)
        update('<footer', '</footer>', newFooter)
        update('<dl id="memo-list" class="dl-horizontal">', '</dl>', newMemoList())
        updateSitemap()
    }

    static String getTitle(File file) {
        Matcher matcher = file.text =~ /<title>(.*)<\/title>/
        matcher[0][1]
    }

    static String getDatePublished(File file) {
        Matcher matcher = file.text =~ /<span itemprop="datePublished">(.*)<\/span>/
        matcher[0][1]
    }


    static String getDateModified(File file) {
        Matcher matcher = file.text =~ /<span itemprop="dateModified">(.*)<\/span>/
        matcher[0][1]
    }

    private static newMemoList() {
        """<dl id="memo-list" class="dl-horizontal">
$memoListItems
        </dl>"""
    }

    private static String getMemoListItems() {
        StringBuilder sb = new StringBuilder()

        getAllMemosSortedByDatePublished().each {
            String title = getTitle(it)
            String memoUrl = CreateNewMemo.getMemoUrl(title)
            String datePublished = getDatePublished(it)

            sb.append "            <dt datetime=\"$datePublished\">$datePublished</dt><dd><a href=\"memo/$memoUrl/\">$title</a></dd>\n"
        }

        sb.length = sb.length() - 1
        sb.toString()
    }

    private static update(String start, String end, String newTemplate) {
        getAllIndexFiles('./static-content').each {
            final String text = it.text

            final int startIndex = text.indexOf(start)
            if (startIndex != -1) {
                final String beginning = text.substring(0, startIndex)

                final int endIndex = text.indexOf(end, startIndex) + end.length()
                final String remaining = text.substring(endIndex, text.length())

                it.write(beginning + newTemplate + remaining)
            }
        }
    }

    static void updateSitemap() {
        final path = 'static-content/sitemap.xml'
        new File(path).createNewFile()

        String content = """<?xml version="1.0" encoding="utf-8"?>
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd">
  <url>
    <loc>http://www.svenlange.co.za/</loc>
  </url>
"""
        allMemosSortedByDatePublished.each {
            final title = getTitle(it)
            String memoUrl = CreateNewMemo.getMemoUrl(title)
            String dateModified = getDateModified(it)

            content += """  <url>
    <loc>http://www.svenlange.co.za/memo/$memoUrl</loc>
    <lastmod>$dateModified</lastmod>
  </url>
"""
        }

        content += '</urlset>'

        new File(path).write(content)
    }

    static List<File> getAllMemosSortedByDatePublished() {
        List<File> memos = getAllIndexFiles('./static-content/memo').sort {
            final start = it.text.indexOf('<span itemprop="datePublished">') + 31
            final end = it.text.indexOf('</span>', start)
            it.text.substring(start, end)
        }

        memos = memos.reverse() // from newest to oldest

        return memos
    }

    static List<File> getAllIndexFiles(String path) {
        List<File> memos = []
        new File(path).eachFileRecurse(FILES) {
            if (it.name.endsWith('.html')) {
                memos.add it
            }
        }
        memos
    }
}