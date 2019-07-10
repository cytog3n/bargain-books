package com.cyto.bargainbooks.parser;

import android.util.Log;

import com.cyto.bargainbooks.model.Book;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Used for parsing the information from the XML file exported by @url{https://play.google.com/store/apps/details?id=com.vgm.mylibrary}
 */
public class MyLibraryParser {

    public List<Book> getBooks(InputStream stream) throws EncryptedDocumentException, IOException, InvalidFormatException {
        List<Book> books = new ArrayList<>();

        Workbook wb = WorkbookFactory.create(stream);
        Sheet sheet = wb.getSheetAt(1);

        boolean header = true;
        for (Row row : sheet) {
            if (header) {
                header = false;
            } else {
                Book b = new Book();
                b.setTitle(row.getCell(0).getStringCellValue());
                b.setAuthor(row.getCell(1).getStringCellValue());
                b.setISBN(row.getCell(7).getStringCellValue().replaceAll("\\D+", ""));
                if (b.getTitle() != null && b.getAuthor() != null && b.getISBN() != null &&
                        !b.getTitle().isEmpty() && !b.getAuthor().isEmpty() && !b.getISBN().isEmpty()) {
                    books.add(b);
                } else {
                    Log.w("MylibraryParser", "Missing data, row ignored");
                }
            }
        }

        return books;
    }

}
