package ru.example.gnt.common.base.search

interface SearchActivity {
    fun showSearchView(isShown: Boolean)
    fun setSearchText(searchQuery: String)
    fun registerSearchFragment(instance: SearchFragment)

    fun closeSearchInterface()
}
