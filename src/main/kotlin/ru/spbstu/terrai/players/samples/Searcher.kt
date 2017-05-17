package ru.spbstu.terrai.players.samples

/**попытка модернизации класса DummySeracher'a
 * Выход - единственное, в существовании чего можно не сомневаться.
 * Created by Numay on 15.04.2017.
 */

import ru.spbstu.terrai.core.*
import java.util.*

class Searcher : AbstractPlayer(){//класс-реализация абстрактного игрока-ИИ

    private lateinit var currentLocation: Location //хранит положение на карте

    private val roomMap = mutableMapOf<Location, Room>()//карта хранящая исследованные ботом клетки

    override fun setStartLocationAndSize(location: Location, width: Int, height: Int) {
        super.setStartLocationAndSize(location, width, height)
        currentLocation = location
        roomMap[currentLocation] = Entrance//стартовая точка - вход
    }

    private var lastMove: Move = WaitMove

    private val decisions = mutableListOf<Direction>()//список, хранящий сделанные ходы

    private val pathToExit = mutableListOf<Direction>()//список, хранящий путь от ямы до выхода

    private var wormholes = 0 //считает количество уже пройденных червоточин

    private var maxholes = 0 //хранит количество червоточин на карте
    
    private var pathWasFinded = false //найден ли путь от вормхолла до выхода
    
    private var treasureFinded = false
    //Алгоритм, возвращающий бота к вормхоллу выхода
    private var backInTheFirstWormholeFlag = false
    //Режим связывания всех существующих вормхоллов с их комнатами
    private var bindingFlag = false
    private var pathTemp: MutableList<Direction>? = mutableListOf<Direction>()
    //хранит последний результат движения
    private var lastResult: MoveResult?=null //копия последней полученной информации о состоянии
    private var loopwWh = false //завис ли бот в цикле

    //функция управления движением//работает нормально//пока
    override fun getNextMove(): Move {//если есть любая клетка, в которой бот еще не был, то он идет туда

        var rng = Random();//для выбора направления
        var toUnknownDirection: Direction?

        //Особые ситуации

        if(wormholes>11){//попытка прервать ушедшего в цикл бота
                        //не очень
            if(loopwWh){//для совсем печальных случаев
                bindingFlag = false
                pathWasFinded = false
            }


            var step: Direction
            if(!lastResult!!.successful || lastResult?.room is Wormhole){
                step=Direction.values().first{it + currentLocation !in roomMap}
                decisions+=step
                lastMove=WalkMove(step)
                return lastMove
            }
            step=decisions.last()
            wormholes=0
            roomMap.clear()
            lastMove=WalkMove(step.turnBack())
            loopwWh = true
            return lastMove
        }

        if(backInTheFirstWormholeFlag){//бот нашел клад и должен вернуться ко входу
            var step: Direction
            if (wormholes!=1){
                if(lastResult?.room !is Wormhole && lastResult!!.successful){
                    step=decisions.last()
                    decisions.removeAt(decisions.size - 1)
                    return WalkMove(step.turnBack())
                }
                if(!lastResult!!.successful){
                    step=Direction.values().first{it + currentLocation in roomMap}
                    return WalkMove(step)
                }
            }
            val tempmove = followThePath()
            if(tempmove == null){
                return WaitMove
            }
            return followThePath()!!
        }
        if(bindingFlag){//бот считает дырки
            var tempMove=searching()
            if(tempMove!=null) {
                lastMove = tempMove
                return lastMove
            }
            bindingFlag=false
        }

        //стандартный случай
        if(Direction.values().firstOrNull{it + currentLocation !in roomMap} != null) {
            do {//направление выбирается случайно, а не по списку
                toUnknownDirection = Direction.values().firstOrNull { (it.ordinal == rng.nextInt(4) + 0) && (it + currentLocation !in roomMap)}
            } while (toUnknownDirection == null)//это позволит решить лабиринты типа 5го, при условно бесконечном количестве ходов, конечно
        }
        else toUnknownDirection=null
        lastMove = if (toUnknownDirection != null) {
            decisions += toUnknownDirection
            WalkMove(toUnknownDirection)
        }
        else {//если нет, то бот идет назад
            val lastDecision = decisions.lastOrNull()
            if (lastDecision != null) {
                decisions.removeAt(decisions.size - 1)
                val backDirection = lastDecision.turnBack()
                WalkMove(backDirection)
            }
            else {
                WaitMove
            }
        }

        return lastMove
    }

    //создает копию пути от вормхолла до выхода
    private fun getPathToExit (): Move?{ //ну и возвращает бота к вормхоллу откуда он шел
        var lastStep: Direction
        lastStep = decisions.lastOrNull() ?: return null
        decisions.removeAt(decisions.size - 1)
        pathToExit+=lastStep
        return WalkMove(lastStep.turnBack())
    }
    //повторяет путь от вормхолла до выхода, если не находит выход в конце - возвращается к ближайшему вормхоллу
    //возвращает null, если выход найден
    private fun followThePath (): Move?{
            var step : Direction
            //бот следует по запомненному пути к выходу
            if(lastResult?.room is Wormhole){//свалился в новый вормхолл -> скопировал маршрут заново
                pathTemp = mutableListOf<Direction>()
                pathTemp?.addAll(pathToExit)
            }
            if(!lastResult!!.successful && decisions.size>0)//уперся в стену -> возвратился к вормхоллу
                pathTemp = null
            step = pathTemp?.firstOrNull() ?:
                        if (lastResult?.room is Exit) //прошел путь
                            return null //нашел выход
                        else {//не нашел выход -> возвращается к точке входа в комнату
                            step=decisions.last()
                            decisions.removeAt(decisions.size - 1)
                            return WalkMove(step.turnBack())
                        }
                pathTemp?.removeAt(0)
                decisions += step
                return WalkMove(step)
    }
    //процесс поиска количества вормхоллов для оптимизации поиска
    //возвращает null, когда количество найдено
    private fun searching(): Move?{
        //здесь ищется PathToExit
        if(!pathWasFinded) {
            if (wormholes > 0) {
                var newmove = getPathToExit()
                if (newmove != null)
                    return newmove
            }
            pathWasFinded=true
            roomMap.clear()//считаем с самого начала, смысла в старой карте больше нет
            wormholes=2;//с этого момента начинается анализ карты, имеет смысл также сбросить счетчик
                        //но т.к. мы рассматриваем миры в привязке к вормхоллам, то минимальное количество дырок = 2
        }
            //пропускаем одну комнату, так как, очевидно, что оттуда вход не найти
        if(wormholes==2 || wormholes>11){

            var step: Direction
            if(!lastResult!!.successful || lastResult?.room is Wormhole){
                step=Direction.values().first{it + currentLocation !in roomMap}
                decisions+=step
                return WalkMove(step)
            }
            step=decisions.last()
            if(wormholes>11){
                wormholes=2
                roomMap.clear()
            }
            return WalkMove(step.turnBack())
        }

        var lastmove = followThePath()
        if (lastmove == null){
            maxholes = wormholes-1//число дырок
            return null
        }
        return lastmove
    }
    //функция, определяющая алгоритм поведения бота
    override fun setMoveResult(result: MoveResult) {
        val newLocation = (lastMove as? WalkMove)?.let { it.direction + currentLocation } ?: currentLocation//вычисляет местоположение после хода
        val room = result.room//объект, с которым столкнулся игрок
        lastResult=result
        roomMap[newLocation] = room//запись объекта на карту
        if (result.successful) {//если двигался
            when(room) {
                is Wormhole -> {//если червоточина
                    if(result.condition.exitFind && !pathWasFinded){//случай, когда бот сначала нашел выход, а потом дырку
                        pathToExit += decisions
                        bindingFlag=true//флаг, запускающий алгоритм связывания вормхоллов
                    }
                    decisions.clear()//ходы можно выбрасывать
                    wormholes++
                    if(maxholes!=0){//если мы уже знаем сколько всего у нас вормхолов
                        if(wormholes>maxholes)
                            wormholes=1;//номер комнаты, по сути
                    }
                    currentLocation = Location(wormholes * 100, wormholes * 100)
                    roomMap[currentLocation] = room
                }
                is Exit -> {//выход, очевидно
                    if (!pathWasFinded && wormholes > 0)//когда из червоточины нашел выход
                            bindingFlag=true
                }
                else -> {//если клетка пустая
                    currentLocation = newLocation
                    if(!treasureFinded && result.condition.hasTreasure){//...после того, как бот в ней пошарил
                        treasureFinded=true
                        if(result.condition.exitFind){
                            if(maxholes>0) {//если найден и выход, и дырки
                                backInTheFirstWormholeFlag =true
                            }
                            if(wormholes==0) {//есть веростность, что бот уже прошел мимо выхода, нужно сжигать карту
                                roomMap.clear()
                                decisions.clear()
                            }
                        }
                    }
                }
            }
        }
        else {
            decisions.removeAt(decisions.size - 1)//если стоит на месте
        }
    }

}


fun main(args: Array<String>) {

    var test = SearcherTest()
    test.testLab7()

}
