import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.lendingbuddha.emicalculator.Utiles.MonthlyDetail
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

// Now you can use these classes without redeclaration errors.

// Function to create PDF and save to storage
fun createPDF(monthlyDetails: List<MonthlyDetail>, context: Context) {
    val uniqueID: String = UUID.randomUUID().toString()
    if (monthlyDetails.isEmpty()) {
        Toast.makeText(context, "No details to save.", Toast.LENGTH_SHORT).show()
        return
    }

    val pdfDocument = PdfDocument()
    val paint = Paint().apply { textSize = 12f }
    var pageNumber = 1
    var yPosition = 50f

    // Create a new page
    var page = pdfDocument.startPage(PdfDocument.PageInfo.Builder(595, 842, pageNumber).create())
    val canvas: Canvas = page.canvas

    // Add header to the PDF
    drawHeader(canvas, paint, yPosition)
    yPosition += 40f

    // Add EMI details to the PDF
    for (detail in monthlyDetails) {
        if (yPosition > 800f) {
            pdfDocument.finishPage(page)
            pageNumber++
            yPosition = 50f
            page = pdfDocument.startPage(PdfDocument.PageInfo.Builder(595, 842, pageNumber).create())
            drawHeader(page.canvas, paint, yPosition)
            yPosition += 40f
        }

        drawDetailRow(page.canvas, detail, yPosition, paint)
        yPosition += 40f
    }

    // Finish the page and save the document
    pdfDocument.finishPage(page)
    savePDF(pdfDocument, " EMI_Details_$uniqueID.pdf", context)
}

// Function to draw header
private fun drawHeader(canvas: Canvas, paint: Paint, yPosition: Float) {
    canvas.drawText("Month", 50f, yPosition, paint)
    canvas.drawText("EMI", 100f, yPosition, paint)         // New EMI column
    canvas.drawText("Principal", 200f, yPosition, paint)
    canvas.drawText("Interest", 300f, yPosition, paint)
    canvas.drawText("Balance", 450f, yPosition, paint)
}

// Function to draw detail row
private fun drawDetailRow(canvas: Canvas, detail: MonthlyDetail, yPosition: Float, paint: Paint) {
    canvas.drawText(detail.month.toString(), 50f, yPosition, paint)
    canvas.drawText(formatCurrencyForPDF(detail.emi), 100f, yPosition, paint)   // New EMI value
    canvas.drawText(formatCurrencyForPDF(detail.principal), 200f, yPosition, paint)
    canvas.drawText(formatCurrencyForPDF(detail.interest), 300f, yPosition, paint)
    canvas.drawText(formatCurrencyForPDF(detail.balance), 450f, yPosition, paint)
}


// Function to save PDF to the Documents directory
private fun savePDF(pdfDocument: PdfDocument, fileName: String, context: Context) {
    // Get a content resolver
    val contentResolver = context.contentResolver

    // Prepare the content values
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
    }

    // Insert the file into the MediaStore
    val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
    } else {
        // For Android versions below Q
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)
        try {
            FileOutputStream(file).use { outputStream ->
                pdfDocument.writeTo(outputStream)
                Toast.makeText(context, "PDF saved at ${file.absolutePath}", Toast.LENGTH_LONG).show()
            }
            // Return the file URI for older Android versions
            Uri.fromFile(file)
        } catch (e: IOException) {
            Toast.makeText(context, "Error saving PDF: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            null
        } finally {
            pdfDocument.close()
        }
    }
    uri?.let { pdfUri ->
        try {
            contentResolver.openOutputStream(pdfUri)?.use { outputStream ->
                pdfDocument.writeTo(outputStream)
                Toast.makeText(context, "PDF saved at $fileName", Toast.LENGTH_LONG).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error saving PDF: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            pdfDocument.close()
        }
    } ?: run {
        Toast.makeText(context, "Error: Could not create PDF file.", Toast.LENGTH_LONG).show()
        pdfDocument.close()
    }
}


// Function to format currency (if not already defined)

fun formatCurrencyForPDF(amount: Double): String = "₹${String.format("%.2f", amount)}"
