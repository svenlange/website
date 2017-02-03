package website

import static java.util.Locale.ENGLISH

class CreateNewMemo {

    static String newMemo = new File('./templates/new-memo.html').text

    static void main(String... args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in))
        print 'Memo title: '
        String memoTitle = br.readLine()

        print 'Keywords: '
        String keywords = br.readLine()

        print 'Date: '
        String date = br.readLine()

        newMemo = newMemo.replace('{{datePublished}}', date)
        newMemo = newMemo.replace('{{dateModified}}', date)
        newMemo = newMemo.replace('{{keywords}}', keywords)

        String newUrl = getMemoUrl(memoTitle)
        newMemo = newMemo.replace('{{newUrl}}', newUrl)

        newMemo = newMemo.replace('{{memoTitle}}', memoTitle)

        final String newFolderPath = "./static-content/memo/$newUrl"
        if (new File(newFolderPath).mkdir()) {
            final String newMemoPath = "$newFolderPath/index.html"
            new File(newMemoPath).createNewFile()
            new File(newMemoPath).write(newMemo)
        }

        UpdateEverything.main(args)
    }

    static String getMemoUrl(String memoTitle) {
        final String newUrl = memoTitle.trim()
                .replace('.', '_')
                .replace(' ', '-')
                .replace('(', '')
                .replace(')', '')
                .toLowerCase(ENGLISH)
        newUrl
    }

}