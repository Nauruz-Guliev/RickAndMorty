
# Мобильное приложение "Rick and Morty" 

<p>
    <img src="../master/docs/cover.gif" width="700"/>
</p>




### Введение
Данное мобильное приложение позволяет познакомиться с миром анимационного сериала [Рик и Морти](https://rick-i-morty.online/).
Пользователю доступна подробная информация о персонажах, эпизодах и локациях, которые предоставляются сервисом ["The Rick and Morty Api"](https://rickandmortyapi.com/). 

### Требования к приложению

- [x] Главный экран состоит из нижней навигации с тремя вкладками: персонажи, эпизоды и локации.
- [x] Экран с деталями о персонаже, эпизоде или локации.
- [x] Списки на главных экранах выполнены в виде двух столбцов.
- [x] Фильтрация и поиск данных на главных экранах.
- [x] Обновление данных на каждом экране посредством смахивания сверху вниз (Pull-to-Reresh).
- [x] Кнопка назад на экранах с деталями.
- [x] Скрывание нижней навигации на экранах с деталями.
- [x] Поддержка работы без интернета путём кэширования данных.
- [x] Возможность навигации на предыдущий экран.
- [x] Поддержка [пагинации](https://ru.wikipedia.org/wiki/%D0%9F%D0%B0%D0%B3%D0%B8%D0%BD%D0%B0%D1%86%D0%B8%D1%8F) на главных экранах.
- [x] Наличие начального экрана (Splash Screen).
- [x] Отображение прогресс-индикатора в момент загрузки на всех экранах. 
- [x] Уведомление для пользователя в случае, если не было найдено данных соотвествующих фильтру. 

### Какие темы были затронуты в рамках интенсива и применены в проекте

1) **Git**

Сохранение версий приложения, разделение на ветки для удобства разработки. 

2) **Android studio**

Вся разработка велась в Android Studio, сборщик - Gradle. К тому же использовался файл с раширением [.toml](https://docs.gradle.org/current/userguide/platforms.html) в сочетании с kotlin для настройки зависомстей, что облегчило работу с модулями.

   3,4) **View**

Помимо обычных view (button, edittext) и viewgroup (linear layout, constraint layout) использовались: ChipGroup для фильтра, общий Searchview для поиска, своя реализация Toolbar, MotionLayout для разорачивания списков на экранах с деталями, Coordinator Layout для реализации выезжающего фильтра. Хотелось достигнуть схожего результата, как в примере [Material Backdrop](https://m2.material.io/components/backdrop#usage)

5) **Fragments** 

Весь проект построен на связке 1 activity + 6 fragments. Для реализации Splash Screen использовалась официальная библиотека [Splash Screen Api](https://developer.android.com/develop/ui/views/launch/splash-screen), которая появлиась в Android 12. Навигация между фрагментами реализована с помошью Fragment Manager. За всю навигацию в приложении отвечает класс [Router](https://github.com/Nauruz-Guliev/RickAndMorty/blob/master/app/src/main/java/ru/example/gnt/rickandmorty/navigation/MainRouter.kt), который реализует интерфейсы-роутеры других модулей и предоставляет реализацию этих интерфейсов. 

6) **Recycler View**

Реализация Recycler View есть на каждом экране, отличаются лишь адаптеры. Для главных экранов использовался PagingDataAdapter, который является частью билиотеки [Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview). В качестве Layout Manager выступает Staggerred Layout Manager. Для экранов с деталями использовался List Adapter.


7) **Concurrency**

На экранах с деталями использовалась связка Handler + Looper, так как результат из [Connectivity Manager](https://developer.android.com/reference/android/net/ConnectivityManager) мог приходить не на главном потоке, вся остальная работа с параллелилизмом велась с помощью RxJava и Kotlin Coroutines.


8) **Network**

Работа с сетью производится с помощью [Retrofit 2](https://square.github.io/retrofit/), [OkHttp](https://square.github.io/okhttp/) и [Glide](https://github.com/bumptech/glide). OkHttp понадобился для создания перехватчиков (Interceptor) логирования и отслеживания состояния подключения к интернету. Glide используется для загрузки изображений. 

9) **SQL** 

Для взаимодействия с базой данных использовался [Room](https://developer.android.com/training/data-storage/room). Так как для пагинации понадобился RemoteMediator и в проекте была RxJava, были добавлены дополнительные завимости для Room.

10) **MVVM / MVP /CA**

Приложение спроектировано с использованием шаблона MVVM и чистой архитектуры. Слои явно не знают друг о друге.  View подписана на события (состояние) во View Model. Взаимодействие view и viewmodel происходит с помощью LiveData/Kotlin flow. Приложение разделено на 7 модулей. App - связывает все модули приложения. Data отвечает за взаимодействие с внешними источниками данных. Ui - в основном хранит в себе ресурсы, которые могут понадобится в других модулях. Common - хранит данные, которые могут понадобится в любом из модулей. Оставшиеся 3 - фиче-модули locations, episodes, characters, которые несут в себе основную бизнес- логику. Каждый фиче-модуль разделен на Data, Domain, Di, Presentation. В Data слое - мапперы, реализация репозиториев. В Domain - интерфейсы репозиториев, сущности, usecase'ы. Di - компоненты, модули, интерфейсы зависимостей. Presentation - модели, адаптеры, фрагменты, вью модели. 

11) **Services**

Сервисы не понадобились.

12) **Coroutines** 

Coroutines использовались для асинхронного выполнения запросов в сеть и доступа к базе данных. В основном использовался Dispatcher - IO, который внедрялся в репозитории. Для хранения состояния в view model был применен Kotlin Flow.

13) **RxJava**

RxJava выступала альтернативой Coroutines в местах, где использовалась Java. В качестве Scheduler во всем проекте использовались IO и Android Main Thread. Последний подключался отдельной зависимостью.  

14) **Dagger** 

Зависимости внедрялись с помощью [Dagger 2](https://www.google.com/search?q=dagger+2&rlz=1C1GCEU_ruRU1029RU1029&oq=dagger+2&aqs=chrome..69i57j35i39l2j69i59j0i20i263i512j69i60j69i61j69i60.1033j0j7&sourceid=chrome&ie=UTF-8). Из одного модуля в другой зависимость поставляется посредством интерфейсов. Фиче-модуль содержит интерфейс, App модуль его реализует и отдаёт реализацию. Жизненный цикл компонентов в фиче модулях завязан на вью моделе. 

### Содержание 

1) **Главный экран - Персонажи.** 

    Данный экран содержит список всех персонажей. Каждый элемент списка отображает: статус, имя, изображение, вид и пол.
 По нажатию кнопки фильтра раскрывается меню. Поведение экрана со списком персонажей похоже на Bottom Sheet Fragment. Экран со списком представляет из себя развернутый Bottom Sheet. Нажатие кнопки фильтра отслеживается на уровне активити, которая следит за тем, какой фрагмент активен. Если активен фрагмент, который реализует интерфейс LayoutBackDropManager, то у такого фрагмента будет метод toggle. Активити может этот метод вызывать, чтобы фрагмент развернулся/свернулся.  

<p align="left">
  <img src="../master/docs/characters/character_list.png" width="200"/>
</p>

  1.1 **Фильтрация** 

Применяется фильтр по кнопке "применить"(apply). Сворачивание по крестику не приводит к применению фильтра. Аналогично реализован и сброс фильтра.
    

<p align="left">
  <img src="../master/docs/characters/character_list_filter.png" width="200"/>
</p>


  **Что происходит во время применения фильтра?**

  Данные из Chip Group и всех Edit Text преобразуются в класс на уровне вью модели. Вьюмодель хранит состояние данных и фильтров. Чтобы загрузились новые данные, на уровне фрагмента вызывается метод, который сообщает Remote Mediator об обновлении.


  ```kotlin 
  adapter?.refresh()
  ```
  То есть запрос проходит такой путь: fragment -> viewmodel -> usecase -> repository -> remotemediator -> room and retrofit.

Для внедрения фильтра в конструкторе Remote Mediator использовался [Assisted Inject](https://dagger.dev/dev-guide/assisted-injection.html)

Чтобы данные точно загрузились, необходимо получать одинаковые значения по фильтрам из локального хранилища и из сети. То есть запрос в сеть не может вернуть 5 персонажей, а запрос в базу данных 10. В таком случае скорее всего ничего не загрузится. Чтобы достичь одинакогого результата были использованы следующие методы:
  
  Запрос в базу данных:

```Kotlin
 @Query(
        "SELECT * FROM character " +
                "WHERE (:name IS NULL OR name LIKE '%' || :name  || '%') " +
                "AND (:species IS NULL OR species LIKE '%' || :species  || '%') " +
                "AND (:type IS NULL OR type LIKE '%' || :type  || '%') " +
                "AND (:gender IS NULL OR gender LIKE :gender) " +
                "AND (:status IS NULL OR status LIKE :status) " +
                "ORDER BY id ASC"
    )
    fun getCharactersFilteredPaged(
        name: String?,
        species: String?,
        type: String?,
        status: String?,
        gender: String?
    ): PagingSource<Int, CharacterEntity>
 ```
 
    Параметры фильтра:

  * name - имя
  * species - вид 
  * type - вид 
  * gender - пол
  * status -  статус

  Логика фильтра в запросе для каждого поля совпадает, поэтому рассмотрим запрос на примере с именем. 
  
  ```Kotlin
    (:name IS NULL OR name LIKE '%' || :name  || '%')
  ```
  :name означает, что на это место будет подставлено значение из парамтра метода getCharactersFilteredPaged с таким именем. IS NULL используется для того, чтобы сделать это сравнение необязательным. То есть, если вместо имени пришел Null, то сравнение даст true, что означает, что любое имя соответсвует  фильтру. `LIKE '%' || :name: || `%`` используется для того, чтобы искать совпадение по всему имени. Если пользователь ввел 'ort' то Morty должен соответсвовать такому запросу, так как ort содержится в Morty. 

  Аннотация Query сигнализирует о том, что это SQL запрос. 

  Возвращаемый класс PagingSource необходим для реализации пагинации и используется в [Remote Mediator](https://github.com/Nauruz-Guliev/RickAndMorty/blob/master/feature/characters/src/main/java/ru/example/gnt/characters/data/CharacterRemoteMediator.kt). Типовой параметр Int нужен для индекса сущности, а CharacterEntity - сама сущность. 

  ORDER BY id ASC означает, что возвращаемые данные будут отсортированы по возрастанию поля ID. 
  
  Запрос в сеть: 
  ```Kotlin
    @GET(CHARACTER_END_POINT)
    fun getCharactersByPageFiltered(
        @Query("page") page: String,
        @Query("name") name: String? = null,
        @Query("species") species: String? = null,
        @Query("type") type: String? = null,
        @Query("status") status: String? = null,
        @Query("gender") gender: String? = null,
        ): Call<CharactersResponseModel>
  ```
@Get - гет-запрос. 

CHARACTER_END_POINT - константа, вынесенная в отдельное поле. 

@Query(*зачение*) - значение будет добавлено в качестве query-параметра в ссылку.
Как и в случае с запросом в базу данных, параметры нулабельны. По умолчанию все равны null за исключением page. Page указывает на страницу. В отличие от запроса в базу данных, здесь этот параметр необходим и обязателен. 

Call - обертка ретрофита, с помощью которой можно ассинхронно получить данные. 

CharactersResponseModel - возвращаемая сущность запроса. 

  1.2 **Поиск** 

  Поиск работает без использования фильтра и производится только по имени. По сути отрабатывает метод фильтра с параметром name. 

<p align="left">
  <img src="../master/docs/characters/character_list_search.png" width="200"/>
</p>
  
  **Как реализован поиск?**
  
  Для реализации использовались 2 интерфейса [SearchFragment](../master/common/src/main/java/ru/example/gnt/common/base/search/SearchFragment.kt) и [SearchActivity](../master/common/src/main/java/ru/example/gnt/common/base/search/SearchFragment.kt), которые доступны всем модулям. Фрагмент, в котором доступен поиск, должен реализовать интерфейс SearchFragment вместе с которым появляется метод:

```Kotlin
    fun doSearch(searchQuery: String?)
```
  Активити следит за тем, какой фрагмент активен. Если это фрагмент, реализующий интерфейс SearchFragment, то у такого фрагмента найдется метод doSearch, в который передастся строка из Search View активити. Из метода doSearch строка передается во вьюмодель, где применяется фильтр, описанный раннее. Интерфейс Search Activity применяется для общения фрагмента с активити.

 Рассмотрим пример, когда необходимо из фрагмента сбросить фильтр и закрыть поиск по нажатию кнопки сброса фильтра ("clear filter"):
  
  ```Kotlin
  
 (requireActivity() as? SearchActivity)?.closeSearchInterface()
  
  ```
Помимо сброса фильтра на уровне вьмодели кнопка "clear filter" из фрагмента вызывает активити, которому фрагмент принадлежит, "безопасно" приводит активити к SearchActivity и вызывает метод закрытия поиска. На уровне активити метод выглядит так:

  ```Kotlin 
  override fun closeSearchInterface() {
        searchCloseButton?.callOnClick()
  }
 ```
 Чтобы скрыть интерфейс поиска, моделируется нажатие кнопки закрыть(крестик в SearchView). 
    

2) **Главный экран - Эпизоды.**

    Данный экран содержит список всех эпизодов. Каждый элемент списка отображает: эпизод(код), имя и дату выходу.
    
    Этот экран схож с тем, что на экране с персонажами. Фильтр и поиск открываются аналогично по нажатию соответсвующих кнопок. 

<p align="left">
  <img src="../master/docs/episodes/episodes_list.png" width="200"/>
</p>

 2.1 **Фильтрация** 
    
 Фильтрация на данном экране доступна только по двум полям. Применяется фильтр по кнопке "применить". Нажатие крестика не приводит к применению или сбросу фильтра. Крестик лишь скрывает интерфейс. Помимо количества фильтров, отличие от вкладки с персонажами в том, что Bottom Sheet переходит в состояние STATE_HALF_EXPANDED вместо STATE_COLLAPSED, как это происходит на экране с персонажами. Также меняется видимость списка с alpha 1 до alpha 0.3 в полу-свернутом состоянии. 

<p align="left">
  <img src="../master/docs/episodes/episodes_filter.png" width="200"/>
</p>

 2.2 **Поиск**
  
 Реализация поиска аналогична тому, как это сделано на экране с персонажами и отличий, кроме используемых методов-эндпоинтов - нет. Здесь поиск так же представляет из себя применение фильтра по полю name. Также, если данных не было найдено, есть возможность сбросить фильтр. 


<p align="left">
  <img src="../master/docs/episodes/episodes_search.png" width="200"/>
</p>



3) **Главный экран - Локации.**

Данный экран содержит список всех эпизодов. Каждый элемент списка отображает: Тип, имя и измерение.
        
Список локаций в реализации почти не отличается с персонажами и эпизодами. Фильтр и поиск открываются аналогично по нажатию соответсвующих кнопок. 

<p align="left">
  <img src="../master/docs/locations/location_list.png" width="200"/>
</p>

 3.1 **Фильтрация** 
    
  Фильтр на экране локаций состоит из 3 полей. Работает точно так же, как и на экране с эпизодами. Отличие лишь в вызываемых эндпоинтов у api и методов в базе данных, которые по структуре схожи с тем, что в персонажах и эпизодах.

<p align="left">
  <img src="../master/docs/locations/location_filter.png" width="200"/>
</p>

 3.2 **Поиск**

Поиск так же ничем не отличается от экранов с персонажами и эпизодами. Применяется фильтр по полю name. 
  
<p align="left">
  <img src="../master/docs/locations/location_search.png" width="200"/>
</p>
    

4) **Детальный экран - Персонаж.**
    
Здесь пользователю доступна полная информация о персонаже с возможностью перехода к локации или к месту происхождения персонажа, а также к эпизоду из списка.

<p align="left">
  <img src="../master/docs/characters/character_details.png" width="200"/>
</p>

**Навигация**

Для навигации в модуле characters есть интерфейс:

```Kotlin
interface CharactersRouter {
    fun navigateToCharacterDetails(id: Int)
    fun navigateToLocationDetails(id: Int)
    fun navigateToEpisodeDetails(id: Int)
}
```
Реализует этот интерфейс [главный роутер](../master/app/src/main/java/ru/example/gnt/rickandmorty/navigation/MainRouter.kt) приложения и с помощью Dagger эту реализацию предоставляет. В нем настраиваются транзакции: добавление фрагментов в backstack, создание и передача bundle и сам переход. 

При переходе из экрана с персонажами на другой экран, вызывается метод в главном роутере. Этот метод при помощи fragment manager совершает транзакцию, передав ID. Новый фрагмент (детали локации или эпизода) получает ID из bundle и передаёт дальше посредством Assisted Inject во view model. 

Экран с эпизодами представляет из себя вертикальный список с одним столбцом. 

<p align="left">
  <img src="../master/docs/characters/character_details_list.png" width="200"/>
</p>

Раскрывается он свайпом снизу верх. Был использован Motion Layout. 

 **Загрузка данных**
 
Загрузка данных, а именно эпизодов и локаций происходит посредством использования id, полученных из API. Эти id достаются из ссылок с помощью утилитного класса [UrlIdExtractor](../master/common/src/main/java/ru/example/gnt/common/utils/UrlIdExtractor.kt), который сначала проверяет, пришла ли вообще ссылка, затем отдаёт id полученный из ссылки, либо пустую строку, если не удалось извлечь id.

Для получения списка эпизодов используется другой утилитный класс [ApiQueryGenerator](../master/common/src/main/java/ru/example/gnt/common/utils/ApiQueryGenerator.kt), который 
превращает список из id, в одну строку из всех id, разделенных запятыми. Это необходимо для создания похожего эндпоинта:

```Kotlin
https://rickandmortyapi.com/api/episode/1,2,3,4,12,15    //последние цифры - id
```

Для получениях эпизодов из базы данных используется сам список из id, а не конкатенация из id. Метод выглядит так:

```Kotlin
  @Query("SELECT * FROM episode WHERE id IN (:ids)")
  fun getEpisodes(ids: List<String>?): Maybe<List<EpisodeEntity>>
```
* ids - список из id

Maybe - особый вид Obsrvable в rxJava, который может вернуть null. Это его отличие от Single. С этой целью он и использован, результат будет получен единожды и он может быть null. 

"SELECT * FROM episode WHERE id IN (:ids)" с помощью этого запроса получаются все эпизоды, у которых id входит в список переданных id. 

List<EpisodeEntity> - список эпизодов

Все полученные данные собираются в одну [сущность](../master/feature/characters/src/main/java/ru/example/gnt/characters/presentation/detials/CharacterDetailsModel.kt) и передаются дальше в view model. 
    
5) **Детальный экран - Эпизод.**

Подробная информация об эпизодах представлена на этом экране. 
    
<p align="left">
  <img src="../master/docs/episodes/episodes_details.png" width="200"/>
</p>


**Навигация**

Навигация построена похожим образов, как это сделано на экране с персонажами. Реализуется интерфейс [EpisodesRouter](../master/feature/episodes/src/main/java/ru/example/gnt/episodes/EpisodesRouter.kt)

**Загрузка данных**
    
Получение данных происходит аналогично тому, как это устроено на экране с персонажами. Отличие в том, что используются другие эндпоинты, а также возможности Kotlin, а именно Coroutines + Flow вместо rxJava + LiveData. 

**Список эпизодов**

К тому же доступен список из всех персонажей, относящихся к эпизоду. Расширение происходит аналогично тому, как это реализовано на детальном экране персонажа.

<p align="left">
  <img src="../master/docs/episodes/episodes_characters.png" width="200"/>
</p>
        

6) **Детальный экран - Локация.**

Детали о локации можно найти здесь.

<p align="left">
  <img src="../master/docs/locations/location_details.png" width="200"/>
</p>

**Навигация**

Навигация также строится с помощью интерфейса, который реализуется главным роутером.

**Список персонажей**

Помимо деталей локации, присутсвует список из персонажей, которые встречались в локации. Раскрыть список можно аналогичным жестом, как и на экране с деталями о персонаже. 
    
<p align="left">
  <img src="../master/docs/locations/location_characters.png" width="200"/>
</p>

### Архитектура. 

#### Зависимость модулей

<p align="left">
  <img src="../master/docs/modules.png" width="600"/>
</p>
    
#### Dagger 2 зависимости

Фиче модули получают зависимости из app-модуля с помощью интерфейсов.

<p align="left">
  <img src="../master/docs/dagger_dependencies.png" width="600"/>
</p>


### Техническая спецификация

| Технология  | Использование | Версия |
| ------------- | ------------- | ------------- |
| Room  | Сохранение локальных данных.  | 2.5.1 |
| Retrofit | Запросы в сеть.  | 2.9.0 |
| RxJava | Работа с ассинхронным кодом.  | 3.1.5 |
| Coroutines | Работа с ассинхронным кодом.  | 1.8.20 |
| Glide | Загрузка изображений.  | 4.15.1 |
| Dagger 2 | Внедрение зависимостей.  | 2.45 |
| OkHttp | Перехватчики запросов.  | 4.10.0 |
| Moshi | Сериализация данных.  | 1.14.0 |
