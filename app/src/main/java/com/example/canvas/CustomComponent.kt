package com.example.canvas

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Composable
fun CustomComponent(
    canvasSize: Dp = 300.dp,
    indicatorValue: Int = 0,
    maxIndicatorValue: Int = 100,
    backgroundIndicator: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
    backgroundIndicatorStrokeWidth: Float = 100f,
    foregroundIndicatorColor: Color = MaterialTheme.colorScheme.primary,
    foregroundIndicatorStrokeWidth: Float = 100f,
    bigTextFontSize: TextUnit = MaterialTheme.typography.labelMedium.fontSize,
    bigTextColor: Color = MaterialTheme.colorScheme.onSurface,
    bigTextSuffix: String = "GB",
    smallText: String = "Remaining",
    smallTextFontSize: TextUnit = MaterialTheme.typography.labelSmall.fontSize,
    smallTextColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
) {
    var allowedIndicatorValue by remember { // this will be used to make sure that the value passed doesnt pass the allowed max value
        mutableStateOf(maxIndicatorValue)
    }

    allowedIndicatorValue = if (indicatorValue <= maxIndicatorValue) {//this makes sure that the enter value fit within the initial and max size
        indicatorValue
    } else {
        maxIndicatorValue
    }

    var animatedIndicatorValue by remember {//this holds the state of the foregroundIndicator as it comes up
        mutableStateOf(0f)
    }
    LaunchedEffect(key1 = allowedIndicatorValue){//this makes the value animate when it occurs
        animatedIndicatorValue = allowedIndicatorValue.toFloat()//the target value is changed to a float to make it type safe
    }

    val percentage = (animatedIndicatorValue / maxIndicatorValue) * 100 //this gets the value of the
    val sweepAngle by animateFloatAsState(
        targetValue = (2.4 * percentage).toFloat(),//makes the percentage fit only 240f percent
        animationSpec = tween(1000),
        label = ""
    )

    val receivedValue by animateIntAsState(
        targetValue = allowedIndicatorValue,
        animationSpec = tween(1000),
        label = ""
    )

    val animatedBigTextColor by animateColorAsState(
        targetValue = if (allowedIndicatorValue == 0)
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        else
            bigTextColor,
        animationSpec = tween(1000),
        label = ""
    )

    Column(
        modifier = Modifier
            .size(canvasSize)//size of the Column
            .drawBehind {
                val componentSize = size / 1.25f //size of area that will allow you to draw in it
                backgroundIndicator(
                    componentSize = componentSize,
                    indicatorColor = backgroundIndicator,
                    indicatorStrokeWidth = backgroundIndicatorStrokeWidth
                )
                foregroundIndicator(
                    sweepAngle = sweepAngle,
                    componentSize = componentSize,
                    indicatorColor = foregroundIndicatorColor,
                    indicatorStrokeWidth = foregroundIndicatorStrokeWidth
                )
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        EmbeddedElements(
            bigText = receivedValue,
            bigTextFontSize = bigTextFontSize,
            bigTextColor = animatedBigTextColor,
            bigTextSuffix = bigTextSuffix,
            smallText = smallText,
            smallTextColor = smallTextColor,
            smallTextFontSize = smallTextFontSize
        )
    }
}

fun DrawScope.backgroundIndicator(//extension function that extends the drawscope the drawscope is what is accepted in the drawbehind
    componentSize: Size,
    indicatorColor: Color,//color of background arc
    indicatorStrokeWidth: Float // width of the stroke
){
    drawArc( // this is a composable that makes an arc
        size = componentSize,
        color = indicatorColor,
        startAngle = 150f, // this moves the start of arc from 0 to 150 degrees
        sweepAngle = 240f, // this is where the arc will stop
        useCenter = false, // makes the arc be disjoint from the center
        style = Stroke(
            width = indicatorStrokeWidth,
            cap = StrokeCap.Round
        ),
        topLeft = Offset( // this get the canvas size and them pushes the component from the topleft
            x = (size.width - componentSize.width) / 2f,
            y = (size.height - componentSize.height) / 2f
        )
    )
}

fun DrawScope.foregroundIndicator(//extension function that extends the drawscope the drawscope is what is accepted in the drawbehind
    sweepAngle: Float,
    componentSize: Size,
    indicatorColor: Color,//color of background arc
    indicatorStrokeWidth: Float // width of the stroke
){
    drawArc( // this is a composable that makes an arc
        size = componentSize,
        color = indicatorColor,
        startAngle = 150f, // this moves the start of arc from 0 to 150 degrees
        sweepAngle = sweepAngle, // this is where the arc will stop
        useCenter = false, // makes the arc be disjoint from the center
        style = Stroke(
            width = indicatorStrokeWidth,
            cap = StrokeCap.Round
        ),
        topLeft = Offset( // this get the canvas size and them pushes the component from the topleft
            x = (size.width - componentSize.width) / 2f,
            y = (size.height - componentSize.height) / 2f
        )
    )
}

@Composable
fun EmbeddedElements(
    bigText: Int,
    bigTextFontSize: TextUnit,
    bigTextColor: Color,
    bigTextSuffix: String,
    smallText: String,
    smallTextColor: Color,
    smallTextFontSize: TextUnit
){
    Text(
        text = smallText,
        color = smallTextColor,
        fontSize = smallTextFontSize,
        textAlign = TextAlign.Center
    )
    Text(
        text = "$bigText ${bigTextSuffix.take(2)}",
        color = bigTextColor,
        fontSize = bigTextFontSize,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold
    )
}

@Composable
@Preview(showBackground = true)
fun CustomComponentPreview() {
    CustomComponent()
}