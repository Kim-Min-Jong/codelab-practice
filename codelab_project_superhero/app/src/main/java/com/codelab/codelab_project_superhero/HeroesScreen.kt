package com.codelab.codelab_project_superhero

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.codelab.codelab_project_superhero.model.Hero
import com.codelab.codelab_project_superhero.model.HeroesRepository
import com.codelab.codelab_project_superhero.ui.theme.md_theme_light_onPrimaryContainer
import com.codelab.codelab_project_superhero.ui.theme.md_theme_light_primary

@Composable
fun HeroContent(
    hero: Hero,
    modifier: Modifier = Modifier
) {
    Card(
        // elevation 2dp
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.padding(
            PaddingValues(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .sizeIn(72.dp)
                .padding(16.dp),
        ) {
            Column(
                // 균형 맞추기 (모든 공간 사용)
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(id = hero.nameRes),
                    style = MaterialTheme.typography.displaySmall
                )
                Text(
                    text = stringResource(id = hero.descriptionRes),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = painterResource(id = hero.imageRes),
                    contentDescription = null
                )
            }

        }
    }
}

@Composable
fun MainView(
    modifier: Modifier = Modifier,
    list: List<Hero>,
    contentPadding: PaddingValues
) {
    LazyColumn(
        contentPadding = contentPadding
    ) {
        items(list) { hero ->
            HeroContent(hero = hero)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentWithTopAppBar() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    titleContentColor = Color.Black,
                    containerColor = md_theme_light_primary,
                ),
                title = {
                    Text("SuperHeroes")
                }
            )
        },
    ) { innerPadding ->
        MainView(
            list = HeroesRepository.heroes,
            contentPadding = innerPadding
        )
    }
}
