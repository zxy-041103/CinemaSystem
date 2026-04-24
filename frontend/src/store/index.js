import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    user: JSON.parse(localStorage.getItem('cinema_user') || 'null')
  },
  mutations: {
    SET_USER(state, user) {
      state.user = user
      if (user) {
        localStorage.setItem('cinema_user', JSON.stringify(user))
      } else {
        localStorage.removeItem('cinema_user')
      }
    }
  },
  actions: {
    login({ commit }, user) {
      commit('SET_USER', user)
    },
    logout({ commit }) {
      commit('SET_USER', null)
    }
  },
  getters: {
    isLogin: state => !!state.user,
    isAdmin: state => state.user && state.user.role === 'ADMIN',
    userId: state => state.user ? state.user.id : null
  }
})
