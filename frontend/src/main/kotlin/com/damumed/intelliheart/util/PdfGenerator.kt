package com.damumed.intelliheart.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.damumed.intelliheart.network.dto.AnalysisResponse
import java.io.File
import java.io.FileOutputStream

object PdfGenerator {

    /**
     * Параметр анализа для отображения в таблице
     */
    private data class AnalysisParameter(
        val nameKz: String,
        val nameRu: String,
        val result: String,
        val unit: String,
        val referenceInterval: String
    )

    /**
     * Генерирует PDF-файл для конкретного анализа и сохраняет его в кэш-директорию
     */
    fun generateAnalysisPdf(
        context: Context,
        analysis: AnalysisResponse,
        patientName: String,
        iin: String
    ): File {
        // Создаем сам PDF документ
        val pdfDocument = PdfDocument()

        // Стандартный размер листа A4 при 72 DPI: 595 x 842 точки
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        // Настраиваем кисти для рисования
        val paintText = Paint().apply {
            color = Color.BLACK
            textSize = 12f
            isAntiAlias = true
        }

        val paintHeader = Paint().apply {
            color = Color.parseColor("#0D47A1") // Фирменный синий цвет
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val paintSubHeader = Paint().apply {
            color = Color.DKGRAY
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            isAntiAlias = true
        }

        val paintBoldText = Paint().apply {
            color = Color.BLACK
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val paintLine = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 1f
            style = Paint.Style.STROKE
        }

        val paintBorder = Paint().apply {
            color = Color.BLACK
            strokeWidth = 1.5f
            style = Paint.Style.STROKE
        }

        val paintStamp = Paint().apply {
            color = Color.parseColor("#1565C0") // Синий цвет печати
            strokeWidth = 2f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        // --- 1. ШАПКА КЛИНИКИ ---
        canvas.drawText("INTELLIHEART MEDICAL CENTER", 40f, 60f, paintHeader)
        canvas.drawText("ЗЕРТХАНАЛЫҚ ЗЕРТТЕУЛЕРДІҢ ҚОРЫТЫНДЫСЫ / РЕЗУЛЬТАТЫ ЛАБОРАТОРНЫХ ИССЛЕДОВАНИЙ", 40f, 80f, paintSubHeader)
        canvas.drawLine(40f, 90f, 555f, 90f, paintLine)

        // --- 2. МЕТАДАННЫЕ ПАЦИЕНТА ---
        var currentY = 120f
        canvas.drawText("Пациент (ФИО):", 40f, currentY, paintBoldText)
        canvas.drawText(patientName, 170f, currentY, paintText)
        
        canvas.drawText("Клиника:", 340f, currentY, paintBoldText)
        canvas.drawText(analysis.clinic, 430f, currentY, paintText)

        currentY += 20f
        canvas.drawText("ЖСН / ИИН:", 40f, currentY, paintBoldText)
        canvas.drawText(if (iin.isNotEmpty()) iin else "950812345678", 170f, currentY, paintText)

        canvas.drawText("Күні / Дата:", 340f, currentY, paintBoldText)
        canvas.drawText(analysis.date.toString(), 430f, currentY, paintText)

        currentY += 20f
        canvas.drawText("Анализ түрі / Тип:", 40f, currentY, paintBoldText)
        canvas.drawText(analysis.title, 170f, currentY, paintText)

        canvas.drawText("Статусы:", 340f, currentY, paintBoldText)
        canvas.drawText(analysis.status, 430f, currentY, paintBoldText.apply { color = Color.parseColor("#2E7D32") })
        paintBoldText.color = Color.BLACK // Возвращаем цвет обратно

        currentY += 30f
        canvas.drawLine(40f, currentY, 555f, currentY, paintLine)

        // --- 3. ТАБЛИЦА РЕЗУЛЬТАТОВ ---
        currentY += 30f
        
        // Рисуем рамку таблицы
        val tableTop = currentY - 15f
        val tableBottom = currentY + 160f
        canvas.drawRect(40f, tableTop, 555f, tableBottom, paintBorder)

        // Заголовки колонок
        canvas.drawText("Атауы / Параметр", 45f, currentY, paintBoldText)
        canvas.drawText("Нәтиже / Рез.", 240f, currentY, paintBoldText)
        canvas.drawText("Бірлік / Ед.", 350f, currentY, paintBoldText)
        canvas.drawText("Норма / Референс", 440f, currentY, paintBoldText)
        
        // Линия под заголовками
        canvas.drawLine(40f, currentY + 8f, 555f, currentY + 8f, paintBorder)

        // Вертикальные разделители колонок
        canvas.drawLine(230f, tableTop, 230f, tableBottom, paintLine)
        canvas.drawLine(335f, tableTop, 335f, tableBottom, paintLine)
        canvas.drawLine(425f, tableTop, 425f, tableBottom, paintLine)

        // Получаем список параметров в зависимости от типа анализа
        val parameters = getParametersForAnalysis(analysis.title)

        var rowY = currentY + 30f
        for (param in parameters) {
            // Рисуем название
            canvas.drawText(param.nameKz, 45f, rowY - 5f, paintBoldText.apply { textSize = 10f })
            canvas.drawText(param.nameRu, 45f, rowY + 7f, paintText.apply { textSize = 9f })

            // Рисуем результат, единицы измерения и референс
            canvas.drawText(param.result, 240f, rowY + 2f, paintBoldText.apply { textSize = 11f })
            canvas.drawText(param.unit, 350f, rowY + 2f, paintText.apply { textSize = 10f })
            canvas.drawText(param.referenceInterval, 440f, rowY + 2f, paintText.apply { textSize = 10f })

            // Линия между строками
            canvas.drawLine(40f, rowY + 15f, 555f, rowY + 15f, paintLine)
            rowY += 30f
        }

        // Восстанавливаем размеры шрифтов в кистях
        paintBoldText.textSize = 12f
        paintText.textSize = 12f

        // --- 4. ПЕЧАТЬ И ПОДПИСЬ ---
        val footerY = tableBottom + 40f
        
        // Подпись врача
        canvas.drawText("Жауапты дәрігер / Ответственный врач:", 40f, footerY, paintBoldText)
        canvas.drawLine(260f, footerY, 400f, footerY, paintLine)
        canvas.drawText("С. А. Ахметова", 280f, footerY - 5f, paintSubHeader)

        // Печать клиники
        val stampCenterX = 480f
        val stampCenterY = footerY + 20f
        canvas.drawCircle(stampCenterX, stampCenterY, 35f, paintStamp)
        
        val paintStampText = Paint().apply {
            color = Color.parseColor("#1565C0")
            textSize = 6f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("INTELLIHEART", stampCenterX, stampCenterY - 12f, paintStampText)
        canvas.drawText("* ЗЕРТХАНА *", stampCenterX, stampCenterY, paintStampText)
        canvas.drawText("БЕКІТІЛДІ / М.П.", stampCenterX, stampCenterY + 12f, paintStampText)

        // Нижний колонтитул
        canvas.drawText("Анықтама үшін байланыс телефоны: +7 (727) 330-00-00", 40f, 800f, paintSubHeader)
        canvas.drawText("Құжат электронды түрде жасалған және мөрсіз жарамды.", 40f, 815f, paintSubHeader)

        // Завершаем страницу
        pdfDocument.finishPage(page)

        // Записываем PDF в кэш-директорию приложения
        val directory = File(context.cacheDir, "analyses")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val file = File(directory, "analysis_${analysis.id}.pdf")
        
        try {
            val fos = FileOutputStream(file)
            pdfDocument.writeTo(fos)
            fos.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            pdfDocument.close()
        }

        return file
    }

    /**
     * Заглушка для получения реалистичных данных анализов на основе названия
     */
    private fun getParametersForAnalysis(title: String): List<AnalysisParameter> {
        val lowerTitle = title.lowercase()
        return when {
            lowerTitle.contains("қан") || lowerTitle.contains("крови") || lowerTitle.contains("гемоглобин") -> {
                // Общий анализ крови
                listOf(
                    AnalysisParameter("Гемоглобин (Hb)", "Гемоглобин", "138", "г/л", "120 - 160"),
                    AnalysisParameter("Эритроциттер (RBC)", "Эритроциты", "4.6", "10^12/л", "3.8 - 5.1"),
                    AnalysisParameter("Лейкоциттер (WBC)", "Лейкоциты", "6.2", "10^9/л", "4.0 - 9.0"),
                    AnalysisParameter("Тромбоциттер (PLT)", "Тромбоциты", "242", "10^9/л", "150 - 400"),
                    AnalysisParameter("ЭТЖ (СОЭ)", "Скорость оседания эритроцитов", "9", "мм/ч", "2 - 15")
                )
            }
            lowerTitle.contains("биохим") -> {
                // Биохимический анализ крови
                listOf(
                    AnalysisParameter("Глюкоза (Glucose)", "Глюкоза в плазме", "4.9", "ммоль/л", "4.11 - 5.89"),
                    AnalysisParameter("Холестерин (Chol)", "Холестерин общий", "4.7", "ммоль/л", "3.5 - 5.2"),
                    AnalysisParameter("АЛТ (ALT)", "Аланинаминотрансфераза", "22", "Ед/л", "< 41"),
                    AnalysisParameter("АСТ (AST)", "Аспартатаминотрансфераза", "20", "Ед/л", "< 37"),
                    AnalysisParameter("Билирубин (Bilirubin)", "Билирубин общий", "14.2", "мкмоль/л", "3.4 - 20.5")
                )
            }
            else -> {
                // По умолчанию (общие маркеры)
                listOf(
                    AnalysisParameter("Анализ нәтижесі", "Результат исследования", "Норма", "Сәйкес", "Ауытқусыз"),
                    AnalysisParameter("Диагностикалық статус", "Клинический статус", "Сәтті", "Теріс", "Белсенді"),
                    AnalysisParameter("Патологиялық белгілер", "Патологические признаки", "Жоқ", "Теріс", "Табылмады")
                )
            }
        }
    }
}
