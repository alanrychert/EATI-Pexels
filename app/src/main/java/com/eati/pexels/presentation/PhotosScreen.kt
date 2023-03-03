package com.eati.pexels.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.eati.pexels.R
import com.eati.pexels.domain.Photo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun PhotosScreen(viewModel: PhotosViewModel) {
    val result by viewModel.photosFlow.collectAsState()

    Photos(result, viewModel::updateResults)

}

@Composable
fun Photos(results: List<Photo>, updateResults: (String) -> Unit) {

    LaunchedEffect(Unit) {
        updateResults("architecture")
    }
    Text(
        text = stringResource(R.string.blank_search),
        modifier = Modifier.padding(16.dp)
    )
    PhotosScaffold(results, updateResults)
}

@Composable
fun AppAuthorMessage(shown: Boolean){
    Row (modifier = Modifier.fillMaxHeight(0.2f)
    ) {
        AnimatedVisibility(
            visible = shown,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> -fullHeight },
                animationSpec = tween(durationMillis = 150, easing = LinearOutSlowInEasing)
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> -fullHeight },
                animationSpec = tween(durationMillis = 250, easing = FastOutLinearInEasing)
            )
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colors.secondary,
                elevation = 4.dp
            ) {
                Text(
                    text = stringResource(R.string.app_author_message),
                    color = Color.Black,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun PhotosScaffold(results: List<Photo>,updateResults: (String) -> Unit){
    var appAuthorMessageShown by remember { mutableStateOf(false) }

    suspend fun showAppAuthorMessage() {
        if (!appAuthorMessageShown) {
            appAuthorMessageShown = true
            delay(3000L)
            appAuthorMessageShown = false
        }
    }
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        modifier= Modifier.fillMaxWidth(),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                actions = {
                    IconButton(
                        onClick = {
                            coroutineScope.launch { showAppAuthorMessage() }
                        }){
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null
                    )}
                }
            )
        },
    ) {
        Column {
            SearchRow(updateResults)
            LazyVerticalGrid(columns = GridCells.Fixed(2) ){
                items(results) {
                        photo -> PhotoItem(photo)
                }
            }
        }
    }
    AppAuthorMessage(appAuthorMessageShown)
}

@Composable
fun SearchRow(updateResults: (String) -> Unit){
    var searchInput by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp),
    )
    {
            TextField(
                value = searchInput,
                onValueChange = { searchInput = it },
                label = { Text(stringResource(id = R.string.textfield_label)) },
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .fillMaxWidth(0.9f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            IconButton(
                onClick = { if (searchInput != "") updateResults(searchInput) },
                modifier= Modifier
                    .align(Alignment.CenterVertically)
                    .fillMaxWidth()) {
                Icon(imageVector = Icons.Filled.Search, contentDescription = null)
            }
    }
}

@Composable
fun PhotoItem(photo: Photo){
    var isExpanded by remember { mutableStateOf(false) }
    Column( modifier = Modifier
        .fillMaxSize()
        .clickable { isExpanded = !isExpanded }) {
        AsyncImage(model = photo.imgUrl, contentDescription = null,
            modifier = Modifier
                .size(164.dp)
                .padding(2.dp)
                .align(Alignment.CenterHorizontally),
            contentScale = ContentScale.Crop
        )
        if (isExpanded)
            Text(
                style = MaterialTheme.typography.body2,
                text = stringResource(R.string.image_author,photo.photographer),
                modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}

//@Preview
//@Composable
//fun PhotosPreview(){
//    EATIPexelsTheme {
//        Surface(
//            modifier = Modifier.fillMaxSize(),
//            color = MaterialTheme.colors.background
//        ) {
//            val photos = emptyList<Photo>()
//            //la lambda es para evitar que tire error y no cargue el preview
//            Photos(photos) { x -> x+1  }
//        }
//    }
//}